# Altafedeltium - Supermercato (Compose + MVVM)

App Android in Kotlin con Jetpack Compose e architettura MVVM.

## Funzionalita principali

- Autenticazione: login/registrazione con validazioni nel ViewModel.
- Home ridisegnata:
  - hero card iniziale
  - ricerca prodotto
  - filtri base + filtri avanzati (prezzo massimo, ordinamento)
  - card prodotto piu ricche con azioni `Dettagli`, `Aggiungi`, `Preferiti`
- Dettaglio prodotto con gestione preferiti e aggiunta al carrello.
- Preferiti:
  - lista dedicata
  - apertura dettaglio
  - rimozione preferito
- Carrello completo:
  - incremento/diminuzione quantita
  - rimozione prodotto
  - riepilogo (subtotale, consegna, totale)
  - checkout con selezione posizione e metodo pagamento
- Profilo completo:
  - sezione anagrafica modificabile
  - gestione posizioni/indirizzi (aggiunta, default, rimozione)
  - storico ordini

## File chiave

- `app/src/main/java/com/example/altafedeltium/data/model/Product.kt`
- `app/src/main/java/com/example/altafedeltium/data/model/ShopModels.kt`
- `app/src/main/java/com/example/altafedeltium/data/mock/ProductMockData.kt`
- `app/src/main/java/com/example/altafedeltium/ui/viewmodel/HomeViewModel.kt`
- `app/src/main/java/com/example/altafedeltium/ui/screens/home/HomeScreen.kt`
- `app/src/main/java/com/example/altafedeltium/ui/screens/home/ProductDetailScreen.kt`
- `app/src/main/java/com/example/altafedeltium/ui/screens/home/FavoritesScreen.kt`
- `app/src/main/java/com/example/altafedeltium/ui/screens/cart/CartScreen.kt`
- `app/src/main/java/com/example/altafedeltium/ui/screens/cart/CheckoutScreen.kt`
- `app/src/main/java/com/example/altafedeltium/ui/screens/profile/ProfileScreen.kt`
- `app/src/main/java/com/example/altafedeltium/ui/screens/profile/OrderHistoryScreen.kt`
- `app/src/main/java/com/example/altafedeltium/ui/navigation/AppDestination.kt`
- `app/src/main/java/com/example/altafedeltium/ui/SupermarketApp.kt`

## Avvio rapido

1. Apri il progetto in Android Studio.
2. Sincronizza Gradle.
3. Avvia l'app su emulator/device.

## Configurazione Google Maps

Per mostrare le mappe reali nella schermata indirizzi/checkout:

1. Vai su Google Cloud Console e abilita `Maps SDK for Android`.
2. Crea una API key e, se vuoi restringerla, usa il package `com.example.altafedeltium` e lo SHA-1 della debug keystore.
3. Nella root del progetto crea o modifica `local.properties` e aggiungi:

```properties
MAPS_API_KEY=AIzaSyYOUR_REAL_API_KEY_HERE
```

4. Ricompila l'app:

```powershell
cd "C:\Users\lucac\Downloads\altafedeltium"
.\gradlew.bat clean
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

Se vuoi un template pronto, copia `local.properties.example` in `local.properties` e sostituisci i valori segnaposto.

## Note utili

- `local.properties` non va committato: contiene path locali e segreti.
- Se la mappa resta vuota, controlla che `Maps SDK for Android` sia abilitato e che il progetto abbia billing attivo.
- Su emulatori usa un'immagine con `Google Play` oppure `Google APIs`.

