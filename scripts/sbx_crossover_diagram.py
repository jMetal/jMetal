"""Generate a diagram illustrating SBX (Simulated Binary Crossover) on real variables.

The sampling replicates SBXCrossover.doCrossover
(jmetal-core .../operator/crossover/impl/SBXCrossover.java) for a single variable:
for a uniform random value u the contracting/expanding factor betaq is derived from
the distribution index, producing the two children c1 and c2. Sampling many u values
shows the offspring probability distribution, which concentrates around the parents
and gets tighter as the distribution index grows.
"""
import os
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt


def sbx_offspring(x1, x2, lb, ub, eta, u):
    """Vectorised SBX for one variable; returns (c1, c2) arrays for the random draws u."""
    y1, y2 = min(x1, x2), max(x1, x2)
    dist = y2 - y1
    np1 = eta + 1.0

    # child 1 (contracting side towards the lower parent)
    beta = 1.0 + 2.0 * (y1 - lb) / dist
    alpha = 2.0 - beta ** (-np1)
    betaq = np.where(u <= 1.0 / alpha,
                     (u * alpha) ** (1.0 / np1),
                     (1.0 / (2.0 - u * alpha)) ** (1.0 / np1))
    c1 = 0.5 * (y1 + y2 - betaq * dist)

    # child 2 (expanding side towards the upper parent)
    beta = 1.0 + 2.0 * (ub - y2) / dist
    alpha = 2.0 - beta ** (-np1)
    betaq = np.where(u <= 1.0 / alpha,
                     (u * alpha) ** (1.0 / np1),
                     (1.0 / (2.0 - u * alpha)) ** (1.0 / np1))
    c2 = 0.5 * (y1 + y2 + betaq * dist)

    c1 = np.clip(c1, lb, ub)
    c2 = np.clip(c2, lb, ub)
    return c1, c2


P1, P2 = 3.0, 7.0
LB, UB = 0.0, 10.0
N = 400_000
rng = np.random.default_rng(42)
u = rng.random(N)

fig, axes = plt.subplots(2, 1, figsize=(10, 6.2), sharex=True)
bins = np.linspace(LB, UB, 121)

for ax, eta, color in ((axes[0], 20.0, "#2c7fb8"), (axes[1], 2.0, "#2c7fb8")):
    c1, c2 = sbx_offspring(P1, P2, LB, UB, eta, u)
    offspring = np.concatenate([c1, c2])
    ax.hist(offspring, bins=bins, density=True, color=color, alpha=0.65,
            edgecolor="none")
    for p, lab in ((P1, "parent 1"), (P2, "parent 2")):
        ax.axvline(p, color="#c0392b", lw=1.8, ls=(0, (4, 3)))
        ax.text(p, ax.get_ylim()[1] * 0.92, " " + lab, color="#c0392b",
                fontsize=9, va="top")
    ax.set_ylabel("offspring density")
    ax.text(0.015, 0.86, f"distribution index  η = {eta:g}", transform=ax.transAxes,
            fontsize=11, fontweight="bold", color="#1a5276")
    ax.set_yticks([])
    ax.spines[["top", "right"]].set_visible(False)

axes[0].text(0.99, 0.60, "large η → offspring close to the parents",
             transform=axes[0].transAxes, ha="right", fontsize=9, color="#555")
axes[1].text(0.99, 0.60, "small η → offspring more spread out",
             transform=axes[1].transAxes, ha="right", fontsize=9, color="#555")
axes[1].set_xlabel("variable value      (bounds [%g, %g])" % (LB, UB))

fig.suptitle("SBX – Simulated Binary Crossover", fontsize=16, fontweight="bold")
fig.text(0.5, 0.005,
         "Deb & Agrawal (1995) · real encoding · 2 parents → 2 children · "
         "offspring distribution sampled from the operator",
         ha="center", fontsize=9, color="#555")
fig.tight_layout(rect=(0, 0.03, 1, 0.97))

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "sbx_crossover.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("parents:", P1, P2, "bounds:", (LB, UB))
