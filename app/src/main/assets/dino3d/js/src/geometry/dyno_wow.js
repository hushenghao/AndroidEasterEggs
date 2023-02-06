load_manager.set_loader('dyno_death', ['ground'], function() {
  let parser = new vox.Parser();

  let frames = {
  	"wow": null,
  	"wow-down": null
  };
  let framesItems = Object.keys(frames);
  let loaded = 0;

  for(let i = 0; i < framesItems.length; i++) {
    // load all flowers
    parser.parse(config.base_path + 'objects/t-rex/other/' + framesItems[i] + '.vox').then(function(voxelData) {
      let builder = new vox.MeshBuilder(voxelData, {voxelSize: .1});
      let material = new THREE.MeshLambertMaterial();
      material.map = vox.MeshBuilder.textureFactory.getTexture(voxelData);
      builder.material = material;

      let mesh = builder.createMesh();
      mesh.castShadow = true;

      frames[framesItems[i]] = mesh;

      loaded++;
      if(loaded== framesItems.length) {
        load_manager.set_mesh('dyno_death', frames);
        load_manager.set_status('dyno_death', true);

        player.setPlayerDeathFrames(frames);
      }
    });
  }
});