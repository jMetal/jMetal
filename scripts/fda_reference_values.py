"""Usage: python fda_reference_values.py > jmetal-problem/src/test/resources/fda/reference-values.csv

Reference values of FDA1-FDA5 from a transcription of Farina, Deb and Amato (IEEE TEVC, 2004), and
of FDA2mod from Mehnen, Wagner and Rudolph (2006). FDA4 and FDA5 with three objectives are checked
against pymoo (pymoo.vendor.gta) when it is installed."""
import sys
import numpy as np
from numpy import sin, cos, pi

def spherical(y, g, m):
    f = []
    for k in range(1, m + 1):
        v = 1 + g
        for i in range(1, m - k + 1):
            v *= cos(y[i - 1] * pi / 2)
        if k > 1:
            v *= sin(y[m - k] * pi / 2)
        f.append(v)
    return f

def fda(code, x, t, m, size_xi):
    n = len(x)
    if code == 1:
        G = sin(.5 * pi * t); g = 1 + np.sum((x[1:] - G) ** 2)
        return [x[0], g * (1 - np.sqrt(x[0] / g))]
    if code == 2:
        k = 1 + (n - 1) // 2; H = .75 + .7 * sin(.5 * pi * t)
        g = 1 + np.sum(x[1:k] ** 2)
        h = 1 - (x[0] / g) ** (1 / (H + np.sum((x[k:] - H) ** 2)))
        return [x[0], g * h]
    if code == 21:
        k = n // 2 + 1; H = .2 + 4.8 * t ** 2
        g = 1 + np.sum(x[1:k] ** 2) + np.sum((x[k:] + 1) ** 2)
        return [x[0], g * (1 - (x[0] / g) ** H)]
    if code == 3:
        F = 10 ** (2 * sin(.5 * pi * t)); G = abs(sin(.5 * pi * t))
        f1 = np.sum(x[:size_xi] ** F); g = 1 + G + np.sum((x[size_xi:] - G) ** 2)
        return [f1, g * (1 - np.sqrt(f1 / g))]
    G = abs(sin(.5 * pi * t))
    if code == 4:
        return spherical(x[:m - 1], np.sum((x[m - 1:] - G) ** 2), m)
    if code == 5:
        F = 1 + 100 * sin(.5 * pi * t) ** 4
        return spherical(x[:m - 1] ** F, G + np.sum((x[m - 1:] - G) ** 2), m)

def main():
    try:
        from pymoo.vendor import gta
    except ImportError:
        gta = None
    rng = np.random.default_rng(2004)
    taus = [0, 1, 2, 3, 5, 7, 10, 13, 20, 27]   # t = tau / 10 with tau_T = 1, n_T = 10
    cases = [(1, 20, 2, 0), (1, 5, 2, 0), (2, 31, 2, 0), (2, 6, 2, 0), (21, 31, 2, 0),
             (21, 6, 2, 0), (3, 30, 2, 5), (3, 31, 2, 1), (4, 12, 3, 0), (4, 7, 2, 0),
             (4, 14, 5, 0), (5, 12, 3, 0), (5, 7, 2, 0), (5, 14, 5, 0)]
    rows, maxdiff = [], 0.0
    for code, n, m, size_xi in cases:
        lo = np.zeros(n); hi = np.ones(n)
        first = {1: 1, 2: 1, 21: 1, 3: size_xi}.get(code, n)
        lo[first:] = -1
        for tau in taus:
            t = tau / 10
            for _ in range(2):
                x = lo + (hi - lo) * rng.random(n)
                f = fda(code, x, t, m, size_xi)
                if gta is not None and code in (4, 5) and m == 3:
                    fp = (gta.FDA4 if code == 4 else gta.FDA5)(x, t)
                    maxdiff = max(maxdiff, np.max(np.abs(np.array(f) - fp) / np.maximum(1, np.abs(fp))))
                rows.append([code, n, m, size_xi, tau, *x, *f])
    print(f"# max relative difference with pymoo (FDA4 and FDA5, 3 objectives): {maxdiff:.2e}",
          file=sys.stderr)
    print("# FDA reference values: problem (21 is FDA2Mod), n, m, |x_I| of FDA3 (0 otherwise),")
    print("# tau (t = tau / 10), the n variables and the m objectives (fda_reference_values.py).")
    for r in rows:
        print(",".join(str(int(v)) if k < 5 else repr(float(v)) for k, v in enumerate(r)))

main()
