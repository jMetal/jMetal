import csv
import plotly.graph_objects as go
from plotly.subplots import make_subplots

def read_csv(path):
    rows = []
    with open(path) as f:
        for row in csv.reader(f):
            rows.append([float(v) for v in row])
    return rows

# --- ZDT4 (2D) ---
fun_zdt4 = read_csv("FUN_ZDT4.csv")
ref_zdt4 = read_csv("resources/referenceFrontsCSV/ZDT4.csv")

fig_zdt4 = go.Figure()
fig_zdt4.add_trace(go.Scatter(
    x=[r[0] for r in ref_zdt4], y=[r[1] for r in ref_zdt4],
    mode="lines", name="True Pareto Front",
    line=dict(color="lightgray", width=2),
))
fig_zdt4.add_trace(go.Scatter(
    x=[r[0] for r in fun_zdt4], y=[r[1] for r in fun_zdt4],
    mode="markers", name=f"RVEA ({len(fun_zdt4)} solutions)",
    marker=dict(size=8, color="royalblue", line=dict(width=1, color="darkblue")),
))
fig_zdt4.update_layout(
    title="RVEA on ZDT4 (jMetal) — 100k evaluations",
    xaxis_title="f₁", yaxis_title="f₂",
    template="plotly_white", width=800, height=550,
    legend=dict(x=0.55, y=0.95),
)
fig_zdt4.show()

# --- DTLZ3 (3D) ---
fun_dtlz3 = read_csv("FUN_DTLZ3.csv")
ref_dtlz3 = read_csv("resources/referenceFrontsCSV/DTLZ3.3D.csv")

fig_dtlz3 = go.Figure()
fig_dtlz3.add_trace(go.Scatter3d(
    x=[r[0] for r in ref_dtlz3], y=[r[1] for r in ref_dtlz3], z=[r[2] for r in ref_dtlz3],
    mode="markers", name="True Pareto Front",
    marker=dict(size=2, color="lightgray", opacity=0.4),
))
fig_dtlz3.add_trace(go.Scatter3d(
    x=[r[0] for r in fun_dtlz3], y=[r[1] for r in fun_dtlz3], z=[r[2] for r in fun_dtlz3],
    mode="markers", name=f"RVEA ({len(fun_dtlz3)} solutions)",
    marker=dict(size=4, color="crimson", line=dict(width=0.5, color="darkred")),
))
fig_dtlz3.update_layout(
    title="RVEA on DTLZ3 (jMetal) — 100k evaluations, 3 objectives",
    scene=dict(xaxis_title="f₁", yaxis_title="f₂", zaxis_title="f₃"),
    template="plotly_white", width=900, height=700,
)
fig_dtlz3.show()
