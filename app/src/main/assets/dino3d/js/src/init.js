/**
 * Initialization of scene, camera, renderer & stats.
 * @type {THREE}
 */
const scene = new THREE.Scene();
if(config.renderer.fog) {
  const color = 0xE7B251; // sandstorm - #FFB934
  const near = 1;
  const far = 175;
  scene.fog = new THREE.Fog(color, near, far);
}

const camera = new THREE.PerspectiveCamera(
	config.camera.fov,
	config.camera.aspect,
	config.camera.near,
	config.camera.far);

const clock = new THREE.Clock();

let input = new InputManager();
let audio = new AudioManager();
let enemy = new EnemyManager();
let score = new ScoreManager();

const renderer = new THREE.WebGLRenderer({
	antialias: config.renderer.antialias,
	alpha: false,
	powerPreference: 'high-performance',
	depth: true});

scene.background = new THREE.Color( 0xE7B251 );

renderer.setSize(config.renderer.width * config.renderer.render_at, config.renderer.height * config.renderer.render_at);
renderer.setPixelRatio( window.devicePixelRatio );

if(config.renderer.shadows) {
	renderer.shadowMap.enabled = true;
	renderer.shadowMap.type = config.renderer.shadows_type;
}

if(config.renderer.toneMapping) {
	renderer.toneMapping = THREE.Uncharted2ToneMapping;
}

renderer.domElement.id = 'three-canvas';
document.body.appendChild(renderer.domElement);

// FPS counter
if(config.renderer.interval !== false && config.renderer.fps_counter === true) {
	var fc = document.createElement('div');
	fc.id = 'fps-counter';

	document.body.appendChild(fc);
}

// Postprocessing
if(config.renderer.postprocessing.enable) {
	var composer = new THREE.EffectComposer(renderer);
	composer.addPass(new THREE.RenderPass(scene, camera));

	if(config.renderer.postprocessing.sao) {
		let saoPass = new THREE.SAOPass( scene, camera, false, true );
		saoPass.params.saoBias = 1;
		saoPass.params.saoIntensity = 0.008;
		saoPass.params.saoScale = 10;
		saoPass.params.saoKernelRadius = 10;
		saoPass.params.saoMinResolution = 0;
		saoPass.params.saoBlur = true;
		saoPass.params.saoBlurRadius = 3;
		saoPass.params.saoBlurStdDev = 42.3;
		saoPass.params.saoBlurDepthCutoff = 0.1;
		composer.addPass( saoPass );
	}
}