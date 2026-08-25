<div align="center">

# sblock

### Controla qué encantamientos existen en tu servidor.

Bloquea encantamientos por completo o limítales el nivel máximo. Los ítems que ya los
tengan se limpian solos.

</div>

---

## Características

- **Bloqueo total** de un encantamiento, o **tope de nivel** (por ejemplo, Filo máximo III).
- **Limpieza automática** — los ítems que llevan un encantamiento bloqueado se corrigen al
  vuelo, sin que nadie tenga que hacer nada.
- **Protección en yunque y mesa de encantar** — no se pueden aplicar los bloqueados.
- **Nombres en español e inglés** — escribe `filo` o `sharpness`, da igual.
- **Autocompletado** en el comando y **recarga en caliente**.
- Mensajes personalizables con MiniMessage y gradientes (vía SiriusCore).

## Comandos

| Comando | Acción |
|---|---|
| `/sblock <encantamiento> <nivel>` | Limita ese encantamiento a un nivel máximo. |
| `/sblock <encantamiento> 0` | Bloquea el encantamiento por completo. |
| `/sblock list` | Lista los encantamientos bloqueados. |
| `/sblock reload` | Recarga la configuración. |

Permiso: `sblock.admin`

## Requisitos

Requiere **[SiriusCore](https://github.com/JhonTova)**. Sin él, el plugin no arranca.

## Instalación

Pon `SiriusCore.jar` y `sblock.jar` en tu carpeta `plugins/` y arranca. Compatible con
Spigot, Paper y Purpur 1.21.11+.
