/**
 * Camera stuff.
 * @type {PerspectiveCamera}
 */
// camera.position.x = 4.978596947741865;
// camera.position.y = 3.105959494533945;
// camera.position.z = 16.081008497413357;

// camera.position.x = 5.863254818047438;
// camera.position.y = 2.684485192305126;
// camera.position.z = 16.91723876945934;

// camera.position.x = 9.22309290766023;
// camera.position.y = 6.395110849684777;
// camera.position.z = 16.24473135101115;

// camera.position.x = 9.92212283449582;
// camera.position.y = 6.879105400618458;
// camera.position.z = 17.710218326572033;

// camera.position.x = 8.910570510124892;
// camera.position.y = 7.244990788859754;
// camera.position.z = 21.644634544601505;

// camera.rotation.x = -0.4702282110618307;
// camera.rotation.y = 0.584284504225838;
// camera.rotation.z = 0.2733367087027698;

camera.position.x = 7.37041093612718;
camera.position.y = 3.428590611619372;
camera.position.z = 22.609984741761778;

camera.rotation.x = -0.2521795322818087;
camera.rotation.y = 0.5626175577081858;
camera.rotation.z = 0.1365832725087437;

if(config.camera.controls) {
	controls.target.set(-1.2946982583264495, -3.0793822864709634e-18, 9.30358864783445);
	controls.update();
}

window.addEventListener( 'resize', onWindowResize, false );

function onWindowResize(){

    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    renderer.setSize( window.innerWidth, window.innerHeight );

}