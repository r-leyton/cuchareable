package com.example.am_s12cuchareable

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var spinnerTipo: Spinner
    private var tipoSeleccionado: String = "Todos"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        spinnerTipo = view.findViewById(R.id.spinnerTipo)
        setupSpinner()
        // Resto igual...
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return view
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        // Centrar el mapa en una ubicación por defecto (ejemplo: Chimbote)
        val chimbote = LatLng(-9.074, -78.593)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(chimbote, 13f))

        // Listener para agregar puntos con long click
        mMap.setOnMapLongClickListener { latLng ->
            showAddPointDialog(latLng)
        }

        // Cargar los puntos desde Firebase
        cargarPuntosDesdeFirestore()
    }

    private fun showAddPointDialog(latLng: LatLng) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Agregar nuevo punto")

        val view = layoutInflater.inflate(R.layout.dialog_add_point, null)
        builder.setView(view)

        val nombreInput = view.findViewById<EditText>(R.id.nombre)
        val descInput = view.findViewById<EditText>(R.id.descripcion)
        val tipoInput = view.findViewById<EditText>(R.id.tipo)

        builder.setPositiveButton("Agregar") { dialog, _ ->
            val data = hashMapOf(
                "nombre" to nombreInput.text.toString(),
                "descripcion" to descInput.text.toString(),
                "latitud" to latLng.latitude,
                "longitud" to latLng.longitude,
                "tipo" to tipoInput.text.toString(),
                "valoraciones" to 0 // Empieza en 0 valoraciones
            )
            FirebaseFirestore.getInstance()
                .collection("puntos")
                .add(data)
                .addOnSuccessListener { cargarPuntosDesdeFirestore() }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun cargarPuntosDesdeFirestore() {
        FirebaseFirestore.getInstance().collection("puntos")
            .orderBy("valoraciones", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                mMap.clear()
                for (document in result) {
                    val tipo = document.getString("tipo") ?: "Otro"
                    // Si está seleccionado "Todos" o el tipo coincide, muestra el punto
                    if (tipoSeleccionado == "Todos" || tipo.equals(tipoSeleccionado, ignoreCase = true)) {
                        val latLng = LatLng(
                            document.getDouble("latitud") ?: 0.0,
                            document.getDouble("longitud") ?: 0.0
                        )
                        val nombre = document.getString("nombre") ?: ""
                        val descripcion = document.getString("descripcion") ?: ""
                        val valoraciones = document.getLong("valoraciones") ?: 0
                        val snippet = "$descripcion\n⭐ $valoraciones valoraciones"
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(nombre)
                                .snippet(snippet)
                                .icon(getIconByTipo(tipo))
                        )
                    }
                }
            }
    }


    private fun getIconByTipo(tipo: String): BitmapDescriptor {
        val resId = when (tipo.lowercase()) {
            "chocho" -> R.drawable.ic_chocho
            "combinado" -> R.drawable.ic_combinado
            "desayuno" -> R.drawable.ic_desayuno
            else -> null
        }
        if (resId != null) {
            val bitmap = BitmapFactory.decodeResource(resources, resId)
            val smallMarker = Bitmap.createScaledBitmap(bitmap, 72, 72, false)
            return BitmapDescriptorFactory.fromBitmap(smallMarker)
        } else {
            return when (tipo.lowercase()) {
                "chocho" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                "combinado" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                "desayuno" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                else -> BitmapDescriptorFactory.defaultMarker()
            }
        }
    }



    private fun setupSpinner() {
        val tipos = listOf("Todos", "Chocho", "Combinado", "Desayuno")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapter

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                tipoSeleccionado = tipos[position]
                cargarPuntosDesdeFirestore() // Recarga puntos al cambiar filtro
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }


}
