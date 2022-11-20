package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment() , OnMapReadyCallback{

    companion object {
        private const val TAG = "SelectLocationFragment"

        private const val FINE_LOCATION_PERMISSION_REQUEST_CODE = 1


    }

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding


    val zoomLevel = 18f


    private lateinit var map: GoogleMap

    private var marker: Marker? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.saveButton.setOnClickListener {

            onLocationSelected()
        }



        return binding.root
    }

    private fun onLocationSelected() {

        marker?.let {
            _viewModel.reminderSelectedLocationStr.value = it.title
            _viewModel.latitude.value = it.position.latitude
            _viewModel.longitude.value = it.position.longitude
        }

        findNavController().popBackStack()


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val zoomLevel = 18f

        // map marker
        map.setOnMapLongClickListener { latLng ->
            setMapMarker(latLng)
            marker!!.showInfoWindow()
        }

        // poi marker
        map.setOnPoiClickListener { poi ->
            setPoiClick(poi)
            marker!!.showInfoWindow()
        }

        // map style
        map.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                requireContext(),
                R.raw.map_style
            )
        )

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            enableMapMyLocation()

        } else {

            requestPermissions(
                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
                FINE_LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun setPoiClick(poi: PointOfInterest) {
        marker?.remove()
        marker = map.addMarker(
            MarkerOptions()
                .position(poi.latLng)
                .title(poi.name)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        )
    }

    private fun setMapMarker(latLng: LatLng) {
        // A Snippet is Additional text that's displayed below the title.
        val snippet = String.format(
            Locale.getDefault(),
            "Lat: %1$.5f, Long: %2$.5f",
            latLng.latitude,
            latLng.longitude
        )

        marker?.remove()
        marker = map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(getString(R.string.dropped_pin))
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        )
    }

    @SuppressLint("MissingPermission")
    private fun enableMapMyLocation() {
        map.isMyLocationEnabled = true
        addLastLocationCallback()
    }

    /* This DOES NOT Work when device location is off*/
    @SuppressLint("MissingPermission")
    private fun addLastLocationCallback() {

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        val lastLocationTask = fusedLocationProviderClient.lastLocation

        // On completion, zoom to the user location and add marker
        lastLocationTask.addOnCompleteListener(requireActivity()) { task ->

            if (task.isSuccessful) {
                val taskResult = task.result
                taskResult?.run {

                    val latLng = LatLng(latitude, longitude)
                    map.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            latLng,
                            zoomLevel
                        )
                    )

                    setMapMarker(latLng)
                }
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                enableMapMyLocation()

            } else {
                _viewModel.showSnackBarInt.value = R.string.permission_denied_explanation
            }
        }
    }
}

