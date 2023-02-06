load_manager.set_loader('misc', ['ground'], function() {
  let parser = new vox.Parser();

  let misc = [];
  let miscItems = ['tumbleweed',
                   'cactus/0', 'cactus/1', 'cactus/2', 'cactus/3', 'cactus/4', 'cactus/5',
                   'desert_skull', 'scorpion',
                   'rocks/0', 'rocks/1', 'rocks/2', 'rocks/3', 'rocks/4',
                   'flowers/0', 'flowers/1', 'flowers/2',
                   'trees/dead', 'trees/green',
                   'fish/0', 'fish/1', 'fish/2', 'seaweed'];

  for(let i = 0; i < miscItems.length; i++) {
    // load all flowers
    parser.parse(config.base_path + 'objects/misc/' + miscItems[i] + '.vox').then(function(voxelData) {
      let builder = new vox.MeshBuilder(voxelData, {voxelSize: .1});
      let material = new THREE.MeshLambertMaterial();
      material.map = vox.MeshBuilder.textureFactory.getTexture(voxelData);
      builder.material = material;
      builder.misc_type = miscItems[i];

      misc[i] = builder;

      if(misc.length == miscItems.length) {
        load_manager.set_vox('misc', misc);
        load_manager.set_status('misc', true);
      }
    });
  }
});