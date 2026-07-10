"""Generate a diagram illustrating uniform mutation on a real variable.

The sampling replicates UniformMutation.doMutation
(jmetal-core .../operator/mutation/impl/UniformMutation.java): the mutated value is
x + (rand - 0.5) * perturbation, i.e. drawn uniformly from a window of width
`perturbation` centred on the current value (then repaired to the bounds).
"""
import os
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt


def uniform_mutation(x, lb, ub, perturbation, u):
    return np.clip(x + (u - 0.5) * perturbation, lb, ub)


X = 5.0
LB, UB = 0.0, 10.0
N = 400_000
rng = np.random.default_rng(19)
u = rng.random(N)

fig, axes = plt.subplots(2, 1, figsize=(10, 5.6), sharex=True)
bins = np.linspace(LB, UB, 121)

for ax, perturbation in ((axes[0], 2.0), (axes[1], 6.0)):
    y = uniform_mutation(X, LB, UB, perturbation, u)
    ax.hist(y, bins=bins, density=True, color="#41ab5d", alpha=0.65, edgecolor="none")
    ax.axvline(X, color="#c0392b", lw=1.8, ls=(0, (4, 3)))
    ax.text(X, ax.get_ylim()[1] * 0.92, " original value x", color="#c0392b",
            fontsize=9, va="top")
    lo, hi = X - perturbation / 2, X + perturbation / 2
    for edge in (lo, hi):
        ax.axvline(edge, color="#444", lw=1.0)
    ax.set_ylabel("mutated-value density")
    ax.text(0.015, 0.84, f"perturbation = {perturbation:g}", transform=ax.transAxes,
            fontsize=11, fontweight="bold", color="#1a5276")
    ax.set_yticks([])
    ax.spines[["top", "right"]].set_visible(False)

axes[0].text(0.99, 0.84, "window  [x − p/2 ,  x + p/2]", transform=axes[0].transAxes,
             ha="right", fontsize=9, color="#555")
axes[1].text(0.99, 0.84, "larger perturbation → wider window",
             transform=axes[1].transAxes, ha="right", fontsize=9, color="#555")
axes[1].set_xlabel("variable value      (bounds [%g, %g])" % (LB, UB))

fig.suptitle("Uniform Mutation", fontsize=16, fontweight="bold")
fig.text(0.5, 0.005,
         "real encoding · uniform perturbation of fixed width centred on the value",
         ha="center", fontsize=9, color="#555")
fig.tight_layout(rect=(0, 0.03, 1, 0.97))

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "uniform_mutation.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("x:", X, "bounds:", (LB, UB))
