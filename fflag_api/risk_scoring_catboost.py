import pandas as pd
from catboost import CatBoostClassifier, Pool
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, accuracy_score
import numpy as np

def run_risk_scoring():
    # Load dataset
    DATA_PATH = 'config/synthetic_feature_flag_all_batches.csv'
    try:
        df = pd.read_csv(DATA_PATH)
    except FileNotFoundError:
        print(f"Error: {DATA_PATH} not found.")
        return {"error": f"{DATA_PATH} not found."}

    # --- Preprocessing & Target Generation ---

    # Since 'risk_score' is not in the CSV, we simulate a Risk Class.
    # Logic: High latency + High error = High Risk.
    def simulate_risk(row):
        # Normalize simplified metrics
        lat = row.get('latency', 0)
        err = row.get('error', 0)
        
        if err > 0 or lat > 300:
            return 'High'
        elif lat > 150:
            return 'Medium'
        else:
            return 'Low'

    print("Simulating 'risk_class' based on latency and error rates...")
    df['risk_class'] = df.apply(simulate_risk, axis=1)

    # Features
    # CatBoost works well with categorical features, but our CSV is mostly numeric/boolean.
    # We will treat booleans as categoricals for CatBoost.

    numeric_features = ['latency', 'cpu', 'error', 'conversion_rate']
    categorical_features = [
        'traffic_low', 'traffic_medium', 'user_web', 
        'region_EU', 'region_LATAM', 'region_US', 
        'release_window_normal', 'release_window_post-release'
    ]

    # Ensure valid data types
    for col in numeric_features:
        df[col] = pd.to_numeric(df[col], errors='coerce').fillna(0)

    # Convert booleans to strings for CatBoost categorical handling
    for col in categorical_features:
        if col in df.columns:
            df[col] = df[col].astype(str)

    features = numeric_features + [c for c in categorical_features if c in df.columns]
    target = 'risk_class'

    X = df[features]
    y = df[target]

    # --- Model Training ---

    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Initialize CatBoost
    model = CatBoostClassifier(
        iterations=500,
        learning_rate=0.1,
        depth=6,
        loss_function='MultiClass',
        verbose=100
    )

    # Identify categorical indices for CatBoost
    cat_features_indices = [X.columns.get_loc(c) for c in categorical_features if c in X.columns]

    print("Training CatBoost Model...")
    model.fit(
        X_train, y_train,
        cat_features=cat_features_indices,
        eval_set=(X_test, y_test),
        early_stopping_rounds=50
    )

    # --- Evaluation ---

    predictions = model.predict(X_test)
    print("\n--- Model Evaluation ---")
    print(f"Accuracy: {accuracy_score(y_test, predictions):.4f}")
    print("\nClassification Report:")
    print(classification_report(y_test, predictions))

    # --- Visualization ---
    import matplotlib
    matplotlib.use('Agg')  # Use non-GUI backend
    import matplotlib.pyplot as plt
    from sklearn.metrics import confusion_matrix, ConfusionMatrixDisplay

    try:
        # 1. Confusion Matrix
        cm = confusion_matrix(y_test, predictions, labels=model.classes_)
        disp = ConfusionMatrixDisplay(confusion_matrix=cm, display_labels=model.classes_)
        
        fig, ax = plt.subplots(figsize=(8, 6))
        disp.plot(cmap='Blues', ax=ax)
        plt.title('Risk Classification Confusion Matrix')
        plt.savefig('frontend/risk_confusion_matrix.png')
        plt.close()

        # 2. Feature Importance
        feature_importance = model.get_feature_importance()
        sorted_idx = np.argsort(feature_importance)
        
        plt.figure(figsize=(10, 6))
        plt.barh(range(len(sorted_idx)), feature_importance[sorted_idx], align='center')
        plt.yticks(range(len(sorted_idx)), np.array(features)[sorted_idx])
        plt.xlabel('Importance Score')
        plt.title('CatBoost Feature Importance')
        plt.tight_layout()
        plt.savefig('frontend/risk_feature_importance.png')
        plt.close()

    except Exception as e:
        print(f"Visualization Error: {e}")

    import json
    report = classification_report(y_test, predictions, output_dict=True)
    # Convert numpy types to Python types
    def convert_to_python(obj):
        if isinstance(obj, dict):
            return {k: convert_to_python(v) for k, v in obj.items()}
        elif isinstance(obj, (int, float)):
            return float(obj) if isinstance(obj, (int, float)) else obj
        else:
            return obj
    
    results = {
        'accuracy': float(accuracy_score(y_test, predictions)),
        'classification_report': convert_to_python(report),
        'image_paths': ['risk_confusion_matrix.png', 'risk_feature_importance.png']
    }
    return results

if __name__ == '__main__':
    run_risk_scoring()

