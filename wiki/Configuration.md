# Configuration

## File Locations

```
config/bbs/
├── assets/          # Custom assets (hot-reloaded)
│   ├── textures/
│   ├── models/
│   ├── audio/
│   ├── shaders/
│   └── strings/
├── settings/        # Configuration files
│   ├── bbs.json
│   └── keybinds.json
├── export/          # Exported content
└── url_cache/       # Cached remote assets

<world>/bbs/
└── films/           # Film data (.dat files)
```

---

## Settings (bbs.json)

| Category | Options |
|----------|---------|
| **UI** | Scale, colors, editor speeds |
| **Recording** | Frame rate, quality options |
| **Rendering** | Chroma sky toggle |
| **Damage Control** | Auto-enable, restore settings |
| **Audio** | Waveform display |

Edit via:
- In-game settings panel
- Direct JSON editing
- `/bbs config set <option> <value>`

---

## Keybinds (keybinds.json)

Customize all keybinds by editing this file or through the controls menu.

---

## Assets

### Supported Formats

| Type | Formats |
|------|---------|
| **Models** | OBJ, BOBJ, VOX, Cubic (Bedrock-style with Molang) |
| **Textures** | PNG |
| **Audio** | OGG |

### Hot Reload

Assets in `config/bbs/assets/` are monitored by the watchdog system. Changes are applied automatically without restart.

### URL Assets

Remote assets can be loaded via URL. They are cached in `config/bbs/url_cache/`.

---

## Film Storage

Films are stored as compressed MapType `.dat` files containing:
- Camera clip data
- Replay keyframes
- Action sequences
- Inventory/HP/XP snapshots

Location: `<world>/bbs/films/<filmId>.dat`
