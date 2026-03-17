import csv
import plotly.graph_objects as go

# Read RVEA Pareto front from FUN.csv
f1, f2 = [], []
with open("FUN.csv") as f:
    reader = csv.reader(f)
    for row in reader:
        f1.append(float(row[0]))
        f2.append(float(row[1]))

# Read true Pareto front
f1_true, f2_true = [], []
with open("resources/referenceFrontsCSV/ZDT1.csv") as f:
    reader = csv.reader(f)
    for row in reader:
        f1_true.append(float(row[0]))
        f2_true.append(float(row[1]))

fig = go.Figure()

fig.add_trace(go.Scatter(
    x=f1_true, y=f2_true,
    mode="lines",
    name="True Pareto Front",
    line=dict(color="lightgray", width=2),
))

fig.add_trace(go.Scatter(
    x=f1, y=f2,
    mode="markers",
    name=f"RVEA ({len(f1)} solutions)",
    marker=dict(size=8, color="royalblue", line=dict(width=1, color="darkblue")),
))

fig.update_layout(
    title="RVEA on ZDT1 (jMetal) — 100k evaluations",
    xaxis_title="f₁",
    yaxis_title="f₂",
    template="plotly_white",
    width=800,
    height=550,
    legend=dict(x=0.55, y=0.95),
)

fig.show()
