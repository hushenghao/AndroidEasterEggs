/**
 * Configuration.
 * @type {Object}
 */
const config = {
	"base_path": "/dino3d/",
	"logs": true,
	"debug": false,
	"camera": {
		"fov": 45,
		"aspect": window.innerWidth/window.innerHeight,
		"near": 0.1,
		"far": 200,
		"controls": false,
		"helper": false
	},
	"renderer": {
		// half size for performance
		"width": window.innerWidth,
		"height": window.innerHeight,
		"render_at": 1, // render resolution (lower - more fps at cost of quality)
		"interval": false, // fps cap (false for no fps limit)
		"fps_counter": true, // only works for fps cap

		// graphics settings
		"antialias": true, // AA
		"shadows": true, // cast shadows (2K only)?
		"shadows_type": THREE.PCFSoftShadowMap,
		"fog": true, // show fog?
		"toneMapping": true, // enable tone mapping (Uncharted2)?
		"effects": true, // daytime, rain, etc
		"postprocessing": {
			"enable": false, // enable postprocessing?
			"sao": false, // Scaling Ambient Occlusion
		}
	},
	"IS_HIDPI": window.devicePixelRatio > 1,

	// iPads are returning "MacIntel" for iOS 13 (devices & simulators).
	// Chrome on macOS also returns "MacIntel" for navigator.platform,
	// but navigator.userAgent includes /Safari/.
	// TODO(crbug.com/998999): Fix navigator.userAgent such that it reliably
	// returns an agent string containing "CriOS".
	"IS_IOS": (/CriOS/.test(window.navigator.userAgent) ||
    /iPad|iPhone|iPod|MacIntel/.test(window.navigator.platform) &&
        !(/Safari/.test(window.navigator.userAgent))),

    "IS_MOBILE": /Android/.test(window.navigator.userAgent) || this.IS_IOS
}