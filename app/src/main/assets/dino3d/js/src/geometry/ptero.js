load_manager.set_loader('ptero', ['ground','cactus'], function() {
  let parser = new vox.Parser();
  let frames = [];
  let framesCount = 5; // including 0

  for(let i = 0; i <= framesCount; i++) {
    // load all .vox frames
    parser.parse(config.base_path + 'objects/ptero/' + i + '.vox').then(function(voxelData) {
      let builder = new vox.MeshBuilder(voxelData, {voxelSize: .1});
      let material = new THREE.MeshLambertMaterial();
      material.map = vox.MeshBuilder.textureFactory.getTexture(voxelData);
      builder.material = material;

      frames[i] = builder;

      if(frames.length - 1 == framesCount) {
        load_manager.set_vox('ptero', frames);
        load_manager.set_status('ptero', true);
      }
    });
  }
});