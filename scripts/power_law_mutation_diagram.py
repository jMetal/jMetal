"""Generate a diagram illustrating power-law mutation on a real variable.

The sampling replicates PowerLawMutation.doMutation
(jmetal-core .../operator/mutation/impl/PowerLawMutation.java):
  tempDelta = rnd^(-delta)
  deltaq    = 0.5 * (rnd - 0.5) * (1 - tempDelta)
  newValue  = x + deltaq * (ub - lb)
This produces heavy-tailed perturbations: mostly small steps with occasional large
jumps, controlled by the exponent delta.
"""
import os
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt


def power_law_mutation(x, lb, ub, delta, rnd, r):
    # Power-distributed strength s in [0, 1]; direction from the relative position t.
    s = rnd ** delta
    t = (x - lb) / (ub - lb)
    down = x - s * (x - lb)
    up = x + s * (ub - x)
    return np.where(t < r, down, up)


X = 5.0
LB, UB = 0.0, 10.0
N = 400_000
rng = np.random.default_rng(23)
rnd = rng.random(N)
r = rng.random(N)

fig, axes = plt.subplots(2, 1, figsize=(10, 5.8), sharex=True)
bins = np.linspace(LB, UB, 201)

for ax, delta in ((axes[0], 0.5), (axes[1], 2.0)):
    y = power_law_mutation(X, LB, UB, delta, rnd, r)
    ax.hist(y, bins=bins, density=True, color="#41ab5d", alpha=0.65, edgecolor="none")
    ax.axvline(X, color="#c0392b", lw=1.8, ls=(0, (4, 3)))
    ax.text(X, ax.get_ylim()[1] * 0.6, " original value x", color="#c0392b",
            fontsize=9, va="top")
    ax.set_yscale("log")
    ax.set_ylim(1e-3, 1e1)
    ax.set_ylabel("density (log)")
    ax.text(0.015, 0.84, f"exponent  delta = {delta:g}", transform=ax.transAxes,
            fontsize=11, fontweight="bold", color="#1a5276")
    ax.spines[["top", "right"]].set_visible(False)

axes[0].text(0.99, 0.84, "delta < 1 → large perturbations (spreads towards the bounds)",
             transform=axes[0].transAxes, ha="right", fontsize=9, color="#555")
axes[1].text(0.99, 0.84, "delta > 1 → small perturbations, occasional large jumps",
             transform=axes[1].transAxes, ha="right", fontsize=9, color="#555")
axes[1].set_xlabel("variable value      (bounds [%g, %g])" % (LB, UB))

fig.suptitle("Power-Law Mutation", fontsize=16, fontweight="bold")
fig.text(0.5, 0.005,
         "real encoding · heavy-tailed perturbation · log scale reveals the tails",
         ha="center", fontsize=9, color="#555")
fig.tight_layout(rect=(0, 0.03, 1, 0.97))

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "power_law_mutation.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("x:", X, "bounds:", (LB, UB))
