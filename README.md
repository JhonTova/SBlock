<div align="center">

# sblock

### Controla qué encantamientos existen en tu servidor.

Bloquea encantamientos por completo o limítales el nivel máximo. Los ítems que ya los
tengan se limpian solos.

[![build](https://github.com/JhonTova/SBlock/actions/workflows/build.yml/badge.svg)](https://github.com/JhonTova/SBlock/actions/workflows/build.yml) [![release](https://img.shields.io/github/v/release/JhonTova/SBlock?label=release)](https://github.com/JhonTova/SBlock/releases/latest) ![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11%2B-62B47A) ![Java](https://img.shields.io/badge/Java-21-E76F00)

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

Requiere **[SiriusCore](https://github.com/JhonTova/SiriusCore)**. Sin él, el plugin no arranca.

## Instalación

Pon `SiriusCore.jar` y `sblock.jar` en tu carpeta `plugins/` y arranca. Compatible con
Spigot, Paper y Purpur 1.21.11+.

## Descarga

El `.jar` de cada versión está en [Releases](https://github.com/JhonTova/SBlock/releases/latest).

## Compilar desde el código

Requiere Java 21 y Maven. SiriusCore no está en un repositorio Maven público, así que
primero se instala en local la versión que declara el `pom.xml`:

```bash
git clone --branch v1.1.1 https://github.com/JhonTova/SiriusCore.git
mvn -f SiriusCore/pom.xml install -DskipTests
mvn package        # en este repositorio; el .jar queda en target/
```

Cada push y pull request se compila en GitHub Actions. Al subir una tag `v*`, el `.jar`
se adjunta solo a la release.
