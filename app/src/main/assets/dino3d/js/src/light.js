/**
 * Light stuff.
 * @type {THREE}
 */
let ALight = new THREE.AmbientLight(0x404040, 2.4);
scene.add( ALight );

let DLight = new THREE.DirectionalLight( 0xffffff, .5 );
let DLightTargetObject = new THREE.Object3D();
DLight.position.set(50,30,-30);
DLight.target = DLightTargetObject;
DLightTargetObject.position.set(-65,-25,-50);

DLight.castShadow = config.renderer.shadows;
DLight.shadow.radius = 1;

/*
 @TODO
 Shadows lower than 2K triggers twitches/flickers on moving objects.
 Better fix this later;
 Maybe with CSM shadows?
 */
DLight.shadow.mapSize.width = 1024 * 3;
DLight.shadow.mapSize.height = 1024 * 3;

DLight.shadow.camera.scale.y = 10;
DLight.shadow.camera.scale.x = 20;
DLight.shadow.camera.near = 0;
DLight.shadow.camera.far = 200;

scene.add(DLight);
scene.add(DLightTargetObject);

if(config.camera.helper) {
	scene.add(new THREE.CameraHelper(DLight.shadow.camera));
}