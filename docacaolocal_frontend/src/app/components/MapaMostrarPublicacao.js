import { MapContainer, TileLayer, Marker } from "react-leaflet";
import "leaflet/dist/leaflet.css";

export default function MapaPublicacao({ lat, lng }) {
  return (
    <div>
      <div className="font-semibold mb-2">Mapa</div>
      <div className="map-container" style={{ position: "relative", zIndex: 1 }}>
        <MapContainer center={[lat, lng]} zoom={15} style={{ height: "200px", width: "100%" }}>
          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
          <Marker position={[lat, lng]} />
        </MapContainer>
      </div>
    </div>
  );
}