# SoulKeep

Paper-плагин: игрок через GUI выбирает, какие предметы «удержать душой» при смерти. Если шанс сработал, предмет убирается из дропа, сохраняется в БД и возвращается после респавна.

## Требования

- Paper 1.21+
- Java 21

## Команды

| Команда | Описание | Право |
|---------|----------|-------|
| `/keepsoul` | Открыть меню защиты | `keepsoul.use` (default: true) |
| `/keepsoul reload` | Перезагрузить конфиги | `keepsoul.reload` (default: op) |
| `/keepsoul debug [игрок]` | Debug-дамп в консоль | `keepsoul.debug` (default: op) |

**Алиасы:** `/ks`, `/soulkeep`

Управление защитой — **только через GUI**. Подкоманд `add` / `remove` / `list` нет.

## GUI (`/keepsoul`)

| Действие | Результат |
|----------|-----------|
| Пустой слот + предмет в руке / на курсоре | Добавить в защиту |
| Клик по защищённому слоту | Убрать из защиты |
| Барьер | Слот закрыт — нужен донат (`keepsouls.slots.N`) |
| Книга внизу | Справка (только читать) |

- Один **тип** предмета — один слот (дубликаты нельзя).
- Закрытые слоты показывают **свой текст** по рангу (`locked-slot-tiers` в `gui/main.yml`).
- Цвета: legacy (`&a`), hex (`&#RRGGBB`, `#RRGGBB`), MiniMessage (`<#RRGGBB>`).

## Права

Выдаются через LuckPerms или любой permission-плагин. Без LuckPerms у **OP** часто открыты все слоты — для проверки зайди без OP.

### Слоты в GUI

Плагин перебирает `keepsouls.slots.1` … `keepsouls.slots.N` (диапазон в `config.yml`) и берёт **максимальный** подходящий номер.

| Право | Эффект |
|-------|--------|
| *(нет)* | `default-slots` слотов (по умолчанию 1) |
| `keepsouls.slots.2` | 2 слота |
| `keepsouls.slots.5` | 5 слотов |

### Бонус к шансу

Аналогично: `keepsouls.boost.10` → +10%, `keepsouls.boost.25` → +25% (берётся максимум).

## Механика смерти

1. Игрок умирает с защищёнными типами в списке.
2. На каждый тип — **один бросок шанса** за смерть.
3. При успехе предмет(ы) убираются из дропа и пишутся в `soul_pending_restore`.
4. После респавна — выдача игроку, строки из БД удаляются (антидюп).

**Шанс:** `settings.default-chance` + бонус с прав + опционально override по типу (`item-overrides`).

**Хранение восстановления:**
- простые стаки без меты → `MATERIAL`
- прочность, чары, имя, NBT → `BINARY` (`serializeAsBytes`)

**`allow-stacks`** (`config.yml`):
- `false` — только тип, 1 шт. при смерти
- `true` — в слот пишется кол-во из руки; при смерти удерживается до этого кол-ва

Нельзя защищать и сохранять при смерти предметы с непустым содержимым (шалкер, bundle и т.п.) — всегда, без настройки.

## Файлы конфигурации

```
plugins/SoulKeep/
├── config.yml           # механика, права, notify, БД
├── gui/
│   └── main.yml         # меню: layout, тексты, барьеры, справка
├── lang/
│   └── messages.yml     # чат-сообщения
└── soulkeep.db          # SQLite (+ .db-wal / .db-shm — норма для WAL)
```

После правок: `/keepsoul reload`. Уже открытое меню закрой и открой заново.

---

## config.yml

```yaml
settings:
  default-chance: 20.0
  allow-stacks: false

item-overrides:
  enabled: true
  TOTEM_OF_UNDYING: 5.0

permissions:
  slots:
    prefix: keepsouls.slots.
    min: 1
    max: 7
    default-slots: 1
  boost:
    prefix: keepsouls.boost.
    min: 1
    max: 100

storage:
  database:
    file: soulkeep.db
    pool-size: 4

notify:
  protection:
    added: true
    removed: true
    # ... каждое сообщение вкл/выкл
  death:
    saved: true
    nothing-saved: true
  gui:
    slot-locked: true
  command:
    reload-done: true
```

- **`item-overrides.enabled: false`** — все типы используют только `default-chance`.
- **`notify.*`** — тихий режим без правки текстов (тексты в `lang/messages.yml`).

---

## gui/main.yml

```yaml
title: "        &#C084FC✦ &#721ddbЗащита душой &#C084FC✦"
rows: 3
slot-positions: [10, 11, 12, 13, 14, 15, 16]

filler-material: PURPLE_STAINED_GLASS_PANE
empty-slot-material: GRAY_STAINED_GLASS_PANE
locked-slot-material: BARRIER

empty-slot-name: "&#8B8B8B◦ &#AAAAAAПустой слот"
protected-slot-name: "&#F3E8FF{material}"
protected-slot-lore: "&#AAAAAAШанс: &#E9D5FF{chance}% &#757575· ЛКМ убрать"

locked-slot-tiers:
  2:
    name: "&#FFD700✦ VIP"
    lore: "&#AAAAAAНужен &#FFD700VIP"
  3:
    name: "&#4FC3F7✦ Premium"
    lore: "&#AAAAAAНужен &#4FC3F7Premium"

info-slot: 22
info-material: KNOWLEDGE_BOOK
info-name: "&#C084FC✦ &#C084FCСправка"
info-lore:
  - ""
  - "&#AAAAAAПоложи предмет в свободный слот —"
  - "&#AAAAAAты отдаёшь ему &#C084FCчасть души&#AAAAAA."
```

- **`slot-positions`** — индексы ячеек в сетке сундука (0–53). Для 3 рядов центр низа = `22`.
- **`locked-slot-tiers`** — ключ = сколько слотов нужно всего (`2` → право `keepsouls.slots.2`).
- **`info-slot: -1`** — убрать кнопку справки.

---

## lang/messages.yml

Чат-сообщения (префикс, добавлено/убрано, смерть, ошибки). Плейсхолдеры: `{prefix}`, `{material}`, `{amount}`, `{chance}`, `{count}`, `{max}`.

Из GUI сюда осталось только **`gui.slot-locked`** — сообщение при клике по закрытому слоту.

---

## База данных

Таблицы:
- **`soul_protection`** — список защиты игрока (`DIAMOND:64,SHEARS` — тип и опционально кол-во)
- **`soul_pending_restore`** — предметы к выдаче после смерти (BLOB)

Файлы `soulkeep.db-wal` / `soulkeep.db-shm` — служебные SQLite (WAL). Не удалять при работающем сервере.

---