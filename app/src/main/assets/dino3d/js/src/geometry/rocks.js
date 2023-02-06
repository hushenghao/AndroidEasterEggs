load_manager.set_loader('rocks', ['ground'], function() {
  let parser = new vox.Parser();

  let rocks = [];
  let rocksCount = 4; // including 0

  for(let i = 0; i <= rocksCount; i++) {
    // load all rocks
    parser.parse(config.base_path + 'objects/rocks/' + i + '.vox').then(function(voxelData) {
      let builder = new vox.MeshBuilder(voxelData, {voxelSize: .1});
      let material = new THREE.MeshLambertMaterial();
      material.map = vox.MeshBuilder.textureFactory.getTexture(voxelData);
      builder.material = material;

      rocks[i] = builder;

      if(rocks.length - 1 == rocksCount) {
        load_manager.set_vox('rocks', rocks);
        load_manager.set_status('rocks', true);
      }
    });
  }
});