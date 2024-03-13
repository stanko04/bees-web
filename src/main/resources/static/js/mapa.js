function getGeoJsonData(geoJsonData) {

    mapboxgl.accessToken = 'pk.eyJ1Ijoic3RhbmtvNDQiLCJhIjoiY2xzYzdsaHh6MG1nczJsbzV0MmFubWtnNyJ9.99PKedbw80WXMSkew8pA5g';
    const map = new mapboxgl.Map({
        container: 'map',
        style: 'mapbox://styles/mapbox/streets-v12',
        center: [19.15324, 48.57442],
        zoom: 6
    });

    map.on('load', () => {
        map.addSource('places', {
            'type': 'geojson',
            'data': geoJsonData
        });
        // Add a layer showing the places.
        map.addLayer({
            'id': 'places',
            'type': 'symbol',
            'source': 'places',
            'layout': {
                'icon-image': ['get', 'icon'],
                'icon-allow-overlap': true,
                'icon-size': 2
            }
        });

        map.on('click', 'places', (e) => {
            // Copy coordinates array.
            const coordinates = e.features[0].geometry.coordinates.slice();
            const description = e.features[0].properties.description;

            while (Math.abs(e.lngLat.lng - coordinates[0]) > 180) {
                coordinates[0] += e.lngLat.lng > coordinates[0] ? 360 : -360;
            }

            new mapboxgl.Popup()
                .setLngLat(coordinates)
                .setHTML(description)
                .addTo(map);
        });

        map.on('mouseenter', 'places', () => {
            map.getCanvas().style.cursor = 'pointer';
        });

        map.on('mouseleave', 'places', () => {
            map.getCanvas().style.cursor = '';
        });
    });

    return map;
}