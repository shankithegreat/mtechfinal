import json
import random
import pandas as pd
from datetime import datetime, timedelta

# ============================================================
# CONFIG
# ============================================================

FEATURE_FLAGS_FILE = "featureflags.json"
OUTPUT_PREFIX = "synthetic_feature_flag"

RANDOM_SEED = 42
random.seed(RANDOM_SEED)

BATCH_SIZES = {
    "batch1": 2000,
    "batch2": 5000,
    "batch3": 5000,
    "batch4": 5000,
    "batch5": 5000,
}

TRAFFIC_LEVELS = ["low", "medium", "high"]
USERS = ["web", "mobile"]
REGIONS = ["US", "EU", "APAC", "LATAM"]

# ============================================================
# LOAD FLAGS (STRICT: featureFlagName only)
# ============================================================

with open(FEATURE_FLAGS_FILE, "r") as f:
    flags = json.load(f)

if not isinstance(flags, list):
    raise ValueError("featureflags.json must be a LIST of flag objects")

FLAG_NAMES = []

for flag in flags:
    if "featureFlagName" not in flag:
        raise ValueError(f"Missing 'featureFlagName' in flag: {flag}")
    FLAG_NAMES.append(flag["featureFlagName"])

if not FLAG_NAMES:
    raise ValueError("No featureFlagName values found in JSON")

# ============================================================
# HELPER FUNCTION
# ============================================================

def base_row():
    return {
        "flag_name": random.choice(FLAG_NAMES),
        "flag_on": random.choices([0, 1], weights=[0.3, 0.7])[0],
        "traffic": random.choices(TRAFFIC_LEVELS, [0.4, 0.35, 0.25])[0],
        "user": random.choice(USERS),
        "region": random.choice(REGIONS),
    }

# ============================================================
# BATCH 1 — BASELINE
# ============================================================

rows = []

for _ in range(BATCH_SIZES["batch1"]):
    r = base_row()

    latency = random.randint(80, 150)
    cpu = random.randint(30, 60)
    error_prob = 0.02

    if r["flag_on"]:
        latency += random.randint(20, 60)
        cpu += random.randint(5, 15)
        error_prob = 0.10

        if r["traffic"] == "high":
            latency += random.randint(80, 200)
            cpu += random.randint(20, 40)
            error_prob = 0.30

    r.update({
        "latency": latency,
        "cpu": cpu,
        "error": random.choices([0, 1], [1 - error_prob, error_prob])[0]
    })

    rows.append(r)

pd.DataFrame(rows).to_csv(f"{OUTPUT_PREFIX}_batch1.csv", index=False)

# ============================================================
# BATCH 2 — HIGH TRAFFIC + REGIONAL STRESS
# ============================================================

rows = []

for _ in range(BATCH_SIZES["batch2"]):
    r = base_row()
    r["traffic"] = random.choices(TRAFFIC_LEVELS, [0.25, 0.35, 0.40])[0]

    latency = random.randint(90, 160)
    cpu = random.randint(35, 65)
    error_prob = 0.03

    if r["flag_on"]:
        latency += random.randint(30, 90)
        cpu += random.randint(10, 25)
        error_prob = 0.10

        if r["traffic"] == "high":
            latency += random.randint(120, 260)
            cpu += random.randint(25, 45)
            error_prob = 0.35

        if r["user"] == "mobile" and r["region"] == "APAC":
            error_prob += 0.08

    r.update({
        "latency": latency,
        "cpu": cpu,
        "error": random.choices([0, 1], [1 - error_prob, error_prob])[0]
    })

    rows.append(r)

pd.DataFrame(rows).to_csv(f"{OUTPUT_PREFIX}_batch2.csv", index=False)

# ============================================================
# BATCH 3 — TIME + RELEASE WINDOW
# ============================================================

rows = []
start_time = datetime(2025, 1, 1)

for _ in range(BATCH_SIZES["batch3"]):
    r = base_row()

    ts = start_time + timedelta(minutes=random.randint(0, 60 * 24 * 60))
    release_window = "post-release" if ts.hour in [10, 11, 12] else "normal"

    latency = random.randint(100, 160)
    cpu = random.randint(40, 65)
    error_prob = 0.03

    if r["flag_on"]:
        latency += random.randint(30, 80)
        cpu += random.randint(10, 25)
        error_prob = 0.10

    if r["traffic"] == "high":
        latency += random.randint(100, 220)
        cpu += random.randint(20, 40)
        error_prob += 0.15

    if release_window == "post-release":
        error_prob += 0.10

    r.update({
        "latency": latency,
        "cpu": cpu,
        "error": random.choices([0, 1], [1 - error_prob, error_prob])[0],
        "timestamp": ts.isoformat(),
        "release_window": release_window
    })

    rows.append(r)

pd.DataFrame(rows).to_csv(f"{OUTPUT_PREFIX}_batch3_time.csv", index=False)

# ============================================================
# BATCH 4 — TOXIC FLAG
# ============================================================

TOXIC_FLAG = random.choice(FLAG_NAMES)
print(f"[INFO] Toxic flag injected: {TOXIC_FLAG}")

rows = []

for _ in range(BATCH_SIZES["batch4"]):
    r = base_row()

    latency = random.randint(100, 170)
    cpu = random.randint(40, 65)
    error_prob = 0.03

    if r["flag_name"] == TOXIC_FLAG and r["flag_on"]:
        latency += random.randint(250, 450)
        cpu += random.randint(40, 60)
        error_prob = 0.60

    r.update({
        "latency": latency,
        "cpu": cpu,
        "error": random.choices([0, 1], [1 - error_prob, error_prob])[0]
    })

    rows.append(r)

pd.DataFrame(rows).to_csv(f"{OUTPUT_PREFIX}_batch4_toxic.csv", index=False)

# ============================================================
# BATCH 5 — BUSINESS KPI (CONVERSION)
# ============================================================

rows = []

for _ in range(BATCH_SIZES["batch5"]):
    r = base_row()

    conversion = random.uniform(0.02, 0.05)

    if r["flag_on"]:
        conversion += random.uniform(0.01, 0.03)

    if r["traffic"] == "high":
        conversion -= random.uniform(0.005, 0.015)

    r.update({
        "latency": random.randint(90, 180),
        "cpu": random.randint(35, 70),
        "error": random.choices([0, 1], [0.96, 0.04])[0],
        "conversion_rate": round(max(conversion, 0), 4)
    })

    rows.append(r)

pd.DataFrame(rows).to_csv(f"{OUTPUT_PREFIX}_batch5_kpi.csv", index=False)

print("All 5 batches generated using featureFlagName.")

files = [
    "synthetic_feature_flag_batch1.csv",
    "synthetic_feature_flag_batch2.csv",
    "synthetic_feature_flag_batch3_time.csv",
    "synthetic_feature_flag_batch4_toxic.csv",
    "synthetic_feature_flag_batch5_kpi.csv",
]

dfs = [pd.read_csv(f) for f in files]

# Union all rows, align columns
merged_df = pd.concat(dfs, axis=0, ignore_index=True, sort=False)

# Optional: consistent column order
column_order = [
    "flag_name",
    "flag_on",
    "traffic",
    "user",
    "region",
    "latency",
    "cpu",
    "error",
    "timestamp",
    "release_window",
    "conversion_rate",
]

merged_df = merged_df.reindex(columns=column_order)
merged_df["conversion_rate"] = merged_df["conversion_rate"].fillna(0)
merged_df["release_window"] = merged_df["release_window"].fillna("none")
merged_df = pd.get_dummies(
    merged_df,
    columns=["traffic", "user", "region", "release_window"],
    drop_first=True
)

merged_df.to_csv("synthetic_feature_flag_all_batches.csv", index=False)

print("Merged dataset shape:", merged_df.shape)

print(merged_df.info())
print(merged_df.isnull().mean().sort_values(ascending=False))