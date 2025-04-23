# 🛏 SleepInventoryKeeper

A Minecraft Spigot plugin that gives your inventory a “checkpoint” every time you sleep — protecting items you had at that time from being lost on death, while any new items picked up after sleep will be dropped.

**WARNING** You should definitely playtest and see if the plugin is functional for your interest, will not be updated, no will any bugs be fixed, also used deprecated durability method for simplicity, so keep in mind

---

## 🔧 How It Works

- When a **player sleeps**, their inventory at that moment is saved.
- If the player **dies**,
  - **Items they had during sleep** are restored on respawn.
  - **New items obtained after sleep** are dropped on death.
- On **respawn**, their saved inventory is restored automatically.

---

## 🧪 Example

1. You sleep with:
   - Iron Sword  
   - Bread (10)  
   - Stone Pickaxe  

2. Later, you find:
   - Diamond  
   - Gold Armor  

3. You die.

➡ On respawn:
- You **keep** your Iron Sword, Bread, and Pickaxe.
- You **drop** the Diamond and Gold Armor.

**Important to note** item amount changes, metadata changes, and similar are also considered when checking for differences, so enchanting, for example, will cause the item to be dropped on death unless you sleep after enchanting, added an exclusion for when durability changes, durability will not be taken into account for item difference checks
