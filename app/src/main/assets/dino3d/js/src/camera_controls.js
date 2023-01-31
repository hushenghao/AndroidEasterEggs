/**
 * Controls initialization.
 * @type {THREE.MapControls}
 */
if(config.camera.controls) {
	var controls = new THREE.MapControls(camera, renderer.domElement);

	controls.enableDamping = true;
	controls.dampingFactor = 0.05;

	controls.screenSpacePanning = false;

	controls.minDistance = 5;
	controls.maxDistance = 100;

	controls.maxPolarAngle = Math.PI / 2;
}