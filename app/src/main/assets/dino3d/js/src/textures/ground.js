load_manager.set_loader('t_ground', [], function() {
	let loader = new THREE.TextureLoader();
	let textures = {
		"top": null
	};
	let loaded_textures = 0;

	loader.load(config.base_path + 'textures/ground_top.png', function ( texture ) {
		texture.magFilter = THREE.NearestFilter;

		texture.wrapS = texture.wrapT = THREE.RepeatWrapping;
		texture.offset.set( 0, 0 );
		texture.repeat.set( 2, 1 );

		textures.top = texture;

		load_manager.set_texture('t_ground', textures);
	    load_manager.set_status('t_ground', true);
	});
});