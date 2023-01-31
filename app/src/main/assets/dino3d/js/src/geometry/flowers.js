load_manager.set_loader('flowers', ['ground'], function() {
  let parser = new vox.Parser();

  let flowers = [];
  let flowersCount = 2; // including 0

  for(let i = 0; i <= flowersCount; i++) {
    // load all flowers
    parser.parse(config.base_path + 'objects/flowers/' + i + '.vox').then(function(voxelData) {
      let builder = new vox.MeshBuilder(voxelData, {voxelSize: .1});
      let material = new THREE.MeshLambertMaterial();
      material.map = vox.MeshBuilder.textureFactory.getTexture(voxelData);
      builder.material = material;

      flowers[i] = builder;

      if(flowers.length - 1 == flowersCount) {
        load_manager.set_vox('flowers', flowers);
        load_manager.set_status('flowers', true);
      }
    });
  }
});