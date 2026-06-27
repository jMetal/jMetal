"""Generate a diagram illustrating Lévy flight mutation on a real variable.

The sampling replicates LevyFlightMutation (jmetal-core
.../operator/mutation/impl/LevyFlightMutation.java): Lévy-distributed steps are
generated with Mantegna's algorithm and scaled by stepSize and the variable range.
The result is a heavy-tailed perturbation: mostly small steps with occasional large
jumps. The figure shows a realization of the steps and the heavy-tailed distribution
compared with a Gaussian.
"""
import os
import math
import numpy as np
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt

BETA = 1.5          # Lévy index (1 < beta <= 2)
STEP_SIZE = 0.02
LB, UB = 0.0, 10.0
X = 5.0             # original value


def levy_steps(n, beta, rng):
    """Mantegna's algorithm, as in LevyFlightMutation.generateLevyStep."""
    num = math.gamma(1 + beta) * math.sin(math.pi * beta / 2)
    den = math.gamma((1 + beta) / 2) * beta * 2 ** ((beta - 1) / 2)
    sigma_u = (num / den) ** (1 / beta)
    u = rng.normal(0, sigma_u, n)
    v = rng.normal(0, 1, n)
    return u / np.abs(v) ** (1 / beta)


rng = np.random.default_rng(11)
N = 400_000
perturbation = levy_steps(N, BETA, rng) * STEP_SIZE * (UB - LB)
mutated = np.clip(X + perturbation, LB, UB)

fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(10.5, 6.0))

# ---------- Panel 1: a realization of the steps ----------
rng2 = np.random.default_rng(5)
seq = levy_steps(80, BETA, rng2) * STEP_SIZE * (UB - LB)
ax1.set_title("1. A realization of consecutive Lévy steps", loc="left",
              fontsize=12.5, fontweight="bold", color="#1a5276")
ax1.stem(np.arange(len(seq)), seq, linefmt="#41ab5d", markerfmt=" ", basefmt="#999999")
ax1.axhline(0, color="#999999", lw=0.8)
ax1.set_xlabel("mutation #")
ax1.set_ylabel("step")
ax1.spines[["top", "right"]].set_visible(False)
ax1.text(0.99, 0.92, "mostly small steps · occasional large jumps",
         transform=ax1.transAxes, ha="right", va="top", fontsize=9, color="#555")

# ---------- Panel 2: heavy-tailed distribution vs Gaussian ----------
ax2.set_title("2. Distribution of the mutated value (log scale reveals the heavy tails)",
              loc="left", fontsize=12.5, fontweight="bold", color="#1a5276")
bins = np.linspace(LB, UB, 201)
ax2.hist(mutated, bins=bins, density=True, color="#41ab5d", alpha=0.65,
         edgecolor="none", label="Lévy flight")
# Gaussian with the same central scale (std of the bulk, |step| within the IQR)
bulk = perturbation[np.abs(perturbation) < np.quantile(np.abs(perturbation), 0.75)]
sigma = bulk.std()
xs = np.linspace(LB, UB, 400)
gauss = np.exp(-0.5 * ((xs - X) / sigma) ** 2) / (sigma * math.sqrt(2 * math.pi))
ax2.plot(xs, gauss, color="#c0392b", lw=1.8, label="Gaussian (same central scale)")
ax2.axvline(X, color="#c0392b", lw=1.2, ls=(0, (4, 3)))
ax2.set_yscale("log")
ax2.set_ylim(1e-3, 1e2)
ax2.set_xlabel("variable value      (bounds [%g, %g])" % (LB, UB))
ax2.set_ylabel("density (log)")
ax2.spines[["top", "right"]].set_visible(False)
ax2.legend(loc="upper right", fontsize=8.5, frameon=False)

fig.suptitle("Lévy Flight Mutation", fontsize=16, fontweight="bold")
fig.text(0.5, 0.005,
         "Mantegna's algorithm · real encoding · β = %.1f, stepSize = %.2f · "
         "heavy-tailed steps" % (BETA, STEP_SIZE),
         ha="center", fontsize=9, color="#555")
fig.tight_layout(rect=(0, 0.03, 1, 0.96))

out = os.path.join(os.path.dirname(__file__), os.pardir,
                   "docs", "resources", "figures", "levy_flight_mutation.png")
fig.savefig(out, dpi=150, bbox_inches="tight")
print("saved:", out)
print("beta:", BETA, "stepSize:", STEP_SIZE, "x:", X)
