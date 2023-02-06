load_manager.set_loader('ground', [], function() {
  let parser = new vox.Parser();

  parser.parse(config.base_path + 'objects/ground sand.vox').then(function(voxelData) {
    let builder = new vox.MeshBuilder(voxelData, {voxelSize: .1});
    let material = new THREE.MeshLambertMaterial();
    material.map = vox.MeshBuilder.textureFactory.getTexture(voxelData);
    builder.material = material;

    load_manager.set_vox('ground', builder);
    load_manager.set_status('ground', true);
  });
});