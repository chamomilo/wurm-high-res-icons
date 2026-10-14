# Settings

Open **Mod updates → High Res Icons → Settings** after the HUD appears. Hover over a parameter for help. Valid edits are saved automatically and applied during play where supported. The shared language selector controls the settings window.

| Key | Default | Valid values |
| --- | --- | --- |
| `replaceIcons` | `true` | `true` / `false` |
| `magicShimmer` | `true` | `true` / `false` |
| `rarityGlow` | `true` | `true` / `false` |
| `rarityBackground` | `false` | `true` / `false` |
| `rarityAnimation` | `blob` | `blob` / `star` |
| `pulseGlow` | `true` | `true` / `false` |
| `glowRadius` | `3` | Integer, 1–4 |
| `glowAlpha` | `0.48` | Number, 0–1 |
| `backgroundAlpha` | `0.16` | Number, 0–1 |
| `rareColor` | `66,153,224` | RGB channels or `#RRGGBB` |
| `supremeColor` | `0,255,255` | RGB channels or `#RRGGBB` |
| `fantasticColor` | `255,0,255` | RGB channels or `#RRGGBB` |
| `resourcePack` | Versioned resource JAR | Non-empty path; restart required |

Editor changes are stored under your user profile in `.chamomilo/mod-settings/wurm-highres.properties`. Saved overrides take precedence over the installation descriptor. Unedited settings use the installed release's defaults, including its versioned resource pack path.

A saved change that requires a client restart immediately shows a warning. **Continue configuring** keeps the game running; **Exit game** saves valid pending edits and opens Wurm's normal quit confirmation. An exit offer remains in the Settings footer while restart changes are pending. Live edits do not apply a pending resource pack change early.

Alternatively, edit `mods/wurm-highres.properties` with the client closed. Remove a setting's saved editor override if you want the descriptor to control that setting again.
