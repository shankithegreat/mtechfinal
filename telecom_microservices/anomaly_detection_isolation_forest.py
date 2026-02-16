import pandas as pd
from sklearn.ensemble import IsolationForest
from sklearn.preprocessing import StandardScaler
import matplotlib.pyplot as plt

# Load dataset
DATA_PATH = 'synthetic_feature_flag_all_batches.csv'
try:
    df = pd.read_csv(DATA_PATH)
except FileNotFoundError:
    print(f"Error: {DATA_PATH} not found.")
    exit()

# --- Preprocessing ---

# 1. Select numeric features available in the CSV
# Actual columns: flag_on, latency, cpu, error, conversion_rate
numeric_features = ['latency', 'cpu', 'error', 'conversion_rate', 'flag_on']

# 2. Select and encode boolean features
# Actual columns: traffic_low, traffic_medium, user_web, region_EU, etc.
bool_features = [
    'traffic_low', 'traffic_medium', 'user_web', 
    'region_EU', 'region_LATAM', 'region_US', 
    'release_window_normal', 'release_window_post-release'
]

# Create a copy for training
X = df.copy()

# Ensure numeric columns are numeric (handle errors if any)
for col in numeric_features:
    X[col] = pd.to_numeric(X[col], errors='coerce').fillna(0)

# Convert boolean columns to 0/1
for col in bool_features:
    if col in X.columns:
        X[col] = X[col].astype(bool).astype(int)

# Combine features
features_to_use = numeric_features + [c for c in bool_features if c in X.columns]
X_train = X[features_to_use]

# Scaling
scaler = StandardScaler()
X_scaled = scaler.fit_transform(X_train)

# --- Model Training ---

# Contamination = expected percentage of outliers (e.g., 5%)
model = IsolationForest(contamination=0.05, random_state=42)

print(f"Training Isolation Forest on {len(X_train)} records using features: {features_to_use}")
model.fit(X_scaled)

# --- Prediction ---

# -1 for outliers, 1 for inliers
df['anomaly_score'] = model.decision_function(X_scaled)
df['is_anomaly'] = model.predict(X_scaled)

# --- Results Analysis ---

anomalies = df[df['is_anomaly'] == -1]

print(f"\nTotal Flags: {len(df)}")
print(f"Anomalies Detected: {len(anomalies)}")

print("\n--- Sample Anomalies ---")
cols_to_show = ['flag_name', 'latency', 'error', 'conversion_rate', 'is_anomaly']
print(anomalies[cols_to_show].head(5).to_string())

# Optional: Visualize anomaly scores distribution
try:
    plt.figure(figsize=(10, 6))
    plt.hist(df['anomaly_score'], bins=50, alpha=0.7)
    plt.title('Anomaly Score Distribution')
    plt.xlabel('Score (lower = more anomalous)')
    plt.ylabel('Frequency')
    plt.show()
except Exception as e:
    print(f"\nCould not display plot: {e}")
