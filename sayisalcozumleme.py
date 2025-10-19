import math


def h(x):
    return math.sqrt(1 + math.sin(x)) / 3


x0 = 2.0
tolerance = 0.01
iteration = 0

while True:
    x1 = h(x0)  # yeni değer
    hata = abs(x1 - x0)
    iteration += 1

    print(f"Iterasyon {iteration}: x0 = {x0:.6f}, x1 = {x1:.6f}, hata = {hata:.6f}")

    if hata < tolerance:
        break
    x0 = x1

print(f"\nYaklaşık Kök = {x1:.6f}\nİterasyon Sayısı: {iteration}")


