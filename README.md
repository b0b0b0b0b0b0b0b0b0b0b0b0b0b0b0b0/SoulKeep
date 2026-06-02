# SoulKeep

Плагин для Paper: в меню игрок выбирает **типы** предметов (например `DIAMOND_SWORD`). При смерти, если шанс сработал, стак убирается из дропа и пишется в `soul_pending_restore`: без ItemMeta — `MATERIAL` + количество; с чарами/именем/NBT — `BINARY` (байты). На респавне строки читаются и удаляются (антидюп). Список защиты — только типы в `soul_protection`.

## Требования

- Paper 1.21+
- Java 21

## Использование

`/keepsoul` — меню защиты.

- Пустой слот + предмет в руке → добавить тип
- Слот с предметом → убрать тип
- Барьер → слот недоступен без пермишена на больше слотов

Алиасы: `/ks`, `/soulkeep` · право `keepsoul.use`

## Пример config.yml

```yaml
settings:
  defaultChance: 20.0

itemOverrides:
  TOTEM_OF_UNDYING: 5.0

permissionBoosts:
  keepsouls.boost.10: 10.0
  keepsouls.boost.25: 25.0

permissionSlots:
  keepsouls.slots.1: 1
  keepsouls.slots.2: 2
  keepsouls.slots.3: 3
  keepsouls.slots.4: 4

storage:
  database:
    file: soulkeep.db
    poolSize: 4

gui:
  title: "&6SoulKeep &8| &7защита душой"
  rows: 3
  slotPositions: [10, 11, 12, 13, 14, 15, 16]
  fillerMaterial: BLACK_STAINED_GLASS_PANE
  emptySlotMaterial: GRAY_STAINED_GLASS_PANE
  lockedSlotMaterial: BARRIER
```

Ключи как в сгенерированном файле (Elytrium, camelCase).  
`slotPositions` — ячейки меню (0–26 в сетке chest). Слоты выше лимита по `keepsouls.slots.*` — `lockedSlotMaterial`.

## Пример messages.yml (GUI)

```yaml
prefix: "&8[&6SoulKeep&8] &r"

gui:
  fillerName: " "
  emptySlotName: "&7Пустой слот"
  lockedSlotName: "&cЗакрыто"
  lockedSlotLore: "&7Нужен донат-ранг"
  protectedSlotName: "&f{material}"
  protectedSlotLore: "&7Шанс: &f{chance}%&7, ЛКМ — убрать"
  slotLocked: "{prefix}&cЭтот слот недоступен. Повысьте ранг."
```
