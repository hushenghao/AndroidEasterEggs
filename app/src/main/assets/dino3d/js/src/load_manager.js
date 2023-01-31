/**
 * Load class.
 * @type {LoadManager}
 */

class LoadManager {
    constructor() {
      this.assets = {};
      this.vox = {}
      this.onload = function() {};
      this.onassetload = function() {};
    }

    set_status(what, status = true) {
      this.assets[what].status = status;

      if(status) {
        logs.log("ASSET LOADED: " + what);

        if(this.onassetload) {
          this.onassetload();
        }
      }

      this.check();
      this.load_deps(what);
    }

    get_status(what) {
      return !!this.assets[what]['status'];
    }

    set_mesh(what, mesh) {
      this.assets[what].mesh = mesh;
    }

    get_mesh(what) {
      return this.assets[what].mesh;
    }

    set_vox(what, vox) {
      this.assets[what].is_vox = true;
      this.assets[what].mesh = vox;
    }

    get_vox(what) {
      return this.assets[what].mesh;
    }

    set_texture(what, texture) {
      this.assets[what].is_texture = true;
      this.assets[what].texture = texture;
    }

    get_texture(what) {
      return this.assets[what].texture;
    }

    get_random_mesh(what) {
      return this.assets[what].mesh[Math.floor(Math.random() * this.assets[what].mesh.length)];
    }

    async wait_for_mesh_material(what, i) {
      let ready = false;

      while(!ready) {
        try {
          this.assets[what].mesh[i].material;
          ready = true;
        } catch(e) {
          await new Promise((resolve, reject) => setTimeout(resolve, 10));
          ready = false;
        }
      }

      return this.assets[what].mesh[i].material;
    }

    async get_mesh_material(what) {
      if(Array.isArray(this.assets[what].mesh)) {
        // return list of material
        let ms = [];

        for(let i = 0; i < this.assets[what].mesh.length; i++) {
          ms.push(await this.wait_for_mesh_material(what, i));
        }

        return ms;
      } else {
        // return material
        return this.assets[what].mesh.material;
      }
    }

    async wait_for_mesh_geometry(what, i) {
      let ready = false;

      while(!ready) {
        try {
          this.assets[what].mesh[i].geometry;
          ready = true;
        } catch(e) {
          await new Promise((resolve, reject) => setTimeout(resolve, 10));
          ready = false;
        }
      }

      return new Promise((resolve, reject) => {
        resolve(this.assets[what].mesh[i].geometry)
      });
    }

    async get_mesh_geometry(what) {
      if(Array.isArray(this.assets[what].mesh)) {
        // return list of geometry
        let gs = [];

        for(let i = 0; i < this.assets[what].mesh.length; i++) {
          gs.push(await this.wait_for_mesh_geometry(what, i));
        }

        return new Promise((resolve, reject) => {
          resolve(gs)
        });
      } else {
        // return geometry
        return new Promise((resolve, reject) => {
          resolve(this.assets[what].mesh.geometry)
        });
      }
    }

    get_certain_mesh(what, type, type_field, return_index = false) {
      if(Array.isArray(type)) {
        // list
        let ms = [];

        for(let i = 0; i < this.assets[what].mesh.length; i++) {
          if(type.includes(this.assets[what].mesh[i][type_field]) || type.includes(this.assets[what].mesh[i][type_field].split('/')[0])) {
            if(return_index) {
              ms.push(i);
            } else {
              ms.push(this.assets[what].mesh[i]);
            }
          }
        }

        return ms;
      } else {
        // single
        for(let i = 0; i < this.assets[what].mesh.length; i++) {
          if(this.assets[what].mesh[i][type_field] == type) {
            if(return_index) {
              return i;
            } else {
              return this.assets[what].mesh[i];
            }
          }
        }
      }
    }

    get_mesh_box(what) {
      if(Array.isArray(this.assets[what].mesh)) {
        // return list of boxes
        let gs = [];

        for(let i = 0; i < this.assets[what].mesh.length; i++) {
          if(this.assets[what].is_vox) {
            // vox
            gs.push(new THREE.Box3().setFromObject(this.assets[what].mesh[i].createMesh()));
          } else {
            // mesh
            gs.push(new THREE.Box3().setFromObject(this.assets[what].mesh[i]));
          }
        }

        return gs;
      } else {
        // return box
        if(this.assets[what].is_vox) {
          // vox
          return new THREE.Box3().setFromObject(this.assets[what].mesh.createMesh());
        } else {
          // mesh
          return new THREE.Box3().setFromObject(this.assets[what].mesh);
        }
      }
    }

    set_loader(what, deps, callback) {
      this.assets[what] = {
        "status": false,
        "callback": callback,
        "mesh": null,
        "is_vox": false,
        "deps": deps
      };
    }

    load_all(callback, assetcallback) {
      logs.log('LOADING ALL ASSETS');
      this.onload = callback;
      this.onassetload = assetcallback;

      for(const asset in this.assets) {
        this.load_asset(asset);
      }
    }

    load_asset(asset) {
      if(this.get_status(asset)) {
        logs.log("ASSET ALREADY LOADED: " + asset, 1);
        return;
      }

      logs.log("LOADING ASSET: " + asset)

      if(this.assets[asset].deps) {
        // check deps
          for(let i in this.assets[asset].deps)
            if(!this.get_status(this.assets[asset].deps[i])) {
              logs.log("LOADING ASSET "+asset+" CANCELED, DEPS YET NOT LOADED: " + this.assets[asset].deps, 1)
              return false; // skip if required asset is not yet loaded
            }
      }

      this.assets[asset].callback(); // load asset
    }

    load_deps(asset) {
      for(let i in this.assets) {
        if(this.assets[i].deps.includes(asset)) {
          this.load_asset(i);
        }
      }
    }

    check() {
      let chk = true;

      for(const asset in this.assets)
        if(!this.assets[asset].status)
          return false;

      logs.log('All assets loaded, starting the game.');
      this.onload();
      return true;
    }

    getLoadPercentage() {
      let total = Object.keys(this.assets).length;
      let loaded = 0;

      for(let k in this.assets) {
        if(this.assets[k].status) {
          loaded++;
        }
      }

      return Math.floor((100 * loaded) / total);
    }
  }