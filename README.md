# Start

Starta JavaFX applikation genom terminalen med kommando

```
mvn clean javafx:run
```


## Miljövariabler

Appen läser hemligheter från miljövariabler, så att inga lösenord ligger i koden. Sätt dem i IntelliJ under Edit Configurations → Environment variables (eller i terminalen innan du startar).

| Variabel | Används till |
|---|---|
| `AUTOCORE_DB_PASSWORD` | Lösenord till MySQL-databasen (krävs, appen startar inte utan) |
| `AUTOCORE_ADMIN_PASSWORD` | Lösenord för testanvändaren `admin` |
| `AUTOCORE_WORKSHOP_PASSWORD` | Lösenord för testanvändaren `workshop` |
| `AUTOCORE_READER_PASSWORD` | Lösenord för testanvändaren `reader` |
| `AUTOCORE_MODE` | Valfri. `standalone` kör utan Spring. Utan den körs Spring-läget |

Lösenorden för testanvändarna används bara första gången, när användarna skapas i databasen. Checka aldrig in run configs eller andra filer med riktiga lösenord.

## LibreTranslate

För att textfält på bokningar ska översättas behöver LibreTranslate köras i Docker. Kör detta en gång i Git Bash:

```
docker run -d --name libretranslate -p 5000:5000 libretranslate/libretranslate --load-only en,sv
```

I Git Bash kan man startar och stoppar med:

```
docker start libretranslate
docker stop libretranslate
```
Eller använd Docker egna app.

Utan LibreTranslate fungerar appen som vanligt, men texten översätts inte.
