# MC-MMO-Essence

Elemental essence/stat system for PaperMC servers. Manages elemental affinity levels per player stored in PDC.

## Stack
- PaperMC 1.21+, Java 21, Gradle (Kotlin DSL)
- Soft-depends: YueMiLibs
- Uses MC-Config-Libs for config.yml migrations

## Element Types
Configurable via config.yml. Default: FIRE, WATER, EARTH, WIND, DARK, LIGHT

## API Access
```java
EssenceApi api = EssenceApiProvider.getApi();
api.getEssence(player, ElementType.FIRE);
api.addEssence(player, ElementType.FIRE, 10);
api.hasEssence(player, ElementType.FIRE, 50);
```

## Key Methods
| Method | Purpose |
|--------|---------|
| `getEssence(player, element)` | Get current essence level |
| `getMaxEssence(player, element)` | Get max essence (from config) |
| `addEssence(player, element, amount)` | Add essence (capped at max) |
| `setEssence(player, element, value)` | Set absolute value |
| `hasEssence(player, element, amount)` | Check minimum threshold |
| `getAllEssence(player)` | Get all essence values |

## Storage
Player PDC: `mmoessence:essence` as comma-separated string (e.g., `"45,30,60,25,10,50"`)

## Config Files
- `config.yml` - Main config with elements definitions and max essence
- `gui/general.yml` - GUI layout settings (title, rows, border)
- `gui/stats.yml` - Element display settings (slots, colors, lore)

## Conventions
- Packages: `org.yuemi.mmoessence.{api,plugin}`
- Classes: `*Api`, `*Provider`, `*Impl`, `*Plugin`, `*Config`
- Config migrations in `plugin.config.migration`
- GUI configs in `plugin.config.gui`
