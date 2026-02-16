import pandas as pd
import xgboost as xgb
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error, r2_score
import numpy as np

# Load dataset
DATA_PATH = 'synthetic_feature_flag_all_batches.csv'
try:
    df = pd.read_csv(DATA_PATH)
except FileNotFoundError:
    print(f"Error: {DATA_PATH} not found.")
    exit()

# --- Preprocessing & Target Generation ---

# Since 'staleness_score' is not in the CSV, we simulate it for demonstration.
# Logic: Flags in 'post-release' with low traffic/usage are "staler".
# This allows the model to learn a pattern for the demo.
def simulate_staleness(row):
    score = 0.0
    if row.get('release_window_post-release', False):
        score += 0.5
    if row.get('traffic_low', False):
        score += 0.3
    if row.get('flag_on') == 0:
        score += 0.1
    # Add random noise
    score += np.random.uniform(0, 0.1)
    return min(1.0, score)

print("Simulating 'staleness_score' for demonstration purposes...")
df['staleness_score'] = df.apply(simulate_staleness, axis=1)

# Features
numeric_features = ['latency', 'cpu', 'error', 'conversion_rate', 'flag_on']
bool_features = [
    'traffic_low', 'traffic_medium', 'user_web', 
    'region_EU', 'region_LATAM', 'region_US', 
    'release_window_normal', 'release_window_post-release'
]

X = df.copy()

for col in numeric_features:
    X[col] = pd.to_numeric(X[col], errors='coerce').fillna(0)

for col in bool_features:
    if col in X.columns:
        X[col] = X[col].astype(bool).astype(int)

features = numeric_features + [c for c in bool_features if c in X.columns]
target = 'staleness_score'

X_data = X[features]
y_data = X[target]

# --- Model Training ---

X_train, X_test, y_train, y_test = train_test_split(X_data, y_data, test_size=0.2, random_state=42)

model = xgb.XGBRegressor(
    objective='reg:squarederror',
    n_estimators=100,
    learning_rate=0.1,
    max_depth=5,
    random_state=42
)

print(f"Training XGBoost Model on {len(X_train)} samples...")
model.fit(X_train, y_train)

# --- Evaluation ---

predictions = model.predict(X_test)
mse = mean_squared_error(y_test, predictions)
r2 = r2_score(y_test, predictions)

print("\n--- Model Evaluation ---")
print(f"Mean Squared Error: {mse:.4f}")
print(f"R2 Score: {r2:.4f}")

print("\n--- Feature Importance ---")
importance = dict(zip(features, model.feature_importances_))
for feature, score in sorted(importance.items(), key=lambda x: x[1], reverse=True):
    print(f"{feature}: {score:.4f}")

# --- Visualization ---
import matplotlib.pyplot as plt

try:
    plt.figure(figsize=(14, 6))

    # 1. Actual vs Predicted
    plt.subplot(1, 2, 1)
    plt.scatter(y_test, predictions, alpha=0.5)
    plt.plot([y_test.min(), y_test.max()], [y_test.min(), y_test.max()], 'r--', lw=2)
    plt.xlabel('Actual Staleness')
    plt.ylabel('Predicted Staleness')
    plt.title('Actual vs Predicted Staleness')

    # 2. Feature Importance
    plt.subplot(1, 2, 2)
    # Sort features by importance
    sorted_idx = np.argsort(model.feature_importances_)
    plt.barh(range(len(sorted_idx)), model.feature_importances_[sorted_idx], align='center')
    plt.yticks(range(len(sorted_idx)), np.array(features)[sorted_idx])
    plt.xlabel('Importance Score')
    plt.title('XGBoost Feature Importance')

    plt.tight_layout()
    plt.show()

except Exception as e:
    print(f"Visualization Error: {e}")

