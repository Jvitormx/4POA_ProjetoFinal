"use client";
import { MapContainer, TileLayer, Marker, useMapEvents } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import { useState } from "react";

function LocationPicker({ setEndereco, setLatLng }) {
  const [position, setPosition] = useState(null);

  useMapEvents({
    click(e) {
      setPosition(e.latlng);
      setLatLng(e.latlng);
      fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${e.latlng.lat}&lon=${e.latlng.lng}`)
        .then(res => res.json())
        .then(data => setEndereco(data.display_name || ""));
    },
  });

  return position ? <Marker position={position} /> : null;
}

export default function MapaPublicacao({ setEndereco, setLatLng }) {
  return (
    <MapContainer center={[-23.55, -46.63]} zoom={13} style={{ height: "250px", width: "100%" }}>
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution="&copy; OpenStreetMap contributors"
      />
      <LocationPicker setEndereco={setEndereco} setLatLng={setLatLng} />
    </MapContainer>
  );
}