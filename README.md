# Start

Starta JavaFX applikation genom terminalen med kommando

```
mvn clean javafx:run
```

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
