 **Cuchareable** 
 
  **DESCRIPCION**
  
app colaborativa basada en Google Maps que permite a los usuarios encontrar, agregar y valorar los mejores puntos para disfrutar de un *chocho*, un *combinado*, un buen desayuno y más delicias populares.  
Ideal para compartir y descubrir experiencias gastronómicas locales, todo de manera fácil y visual.


- **Mapa interactivo:** Basado en Google Maps para ver y explorar puntos en tu ciudad.
- **Colaboración:** Cualquier usuario puede agregar nuevos lugares (long click en el mapa).
- **Tipos de punto:** Identifica lugares para *chocho*, *combinado*, *desayuno* y más, cada uno con su propio icono y/o color.
- **Valoraciones:** Los puntos pueden ser valorados, permitiendo ver los más populares.
- **Filtro por tipo:** Elige ver solo los lugares de un tipo específico o todos a la vez.
- **Interfaz amigable:** Diálogos y botones bonitos usando Material Design.


**🛠️ Tecnologías usadas**
- **Kotlin**
- **Google Maps SDK para Android**
- **Firebase Firestore**
- **Material Components for Android**

**🗺️ Estructura general del proyecto**
- **MainActivity.kt — Carga el fragmento principal con el mapa.**
- **MapsFragment.kt — Fragmento que contiene toda la lógica del mapa, la carga de puntos y el filtrado.**
- **Layouts**
  - **activity_main.xml**
  - **fragment_maps.xml**
  - **dialog_add_point.xml**
- **Recursos**
  - **Iconos personalizados (ic_chocho.png, etc) en res/drawable/**
- **Firebase**
  - **Estructura de colección:**
puntos (Collection)
  └─ id_punto (Document)
      ├─ nombre: String
      ├─ descripcion: String
      ├─ latitud: Double
      ├─ longitud: Double
      ├─ tipo: String
      ├─ valoraciones: Int
 *CAPTURAS**
<br>
<img width="477" height="947" alt="image" src="https://github.com/user-attachments/assets/e00ad9d7-efa3-46d3-9df9-2f9d1a618a69" />
<br>
<img width="451" height="945" alt="image" src="https://github.com/user-attachments/assets/cf855f64-985d-4479-b0d1-1f72c79bfc22" />

