const nebulaSystem = new Nebula.default();


function nebulaCreateDynoDustEmitter(spd = 5) {
    const dynoDustEmitter = new Nebula.Emitter();
    dynoDustEmitter.rate = new Nebula.Rate(
      new Nebula.Span(1, 2),
      new Nebula.Span(0.1, 0.25)
    );

    dynoDustEmitter.addInitializer(new Nebula.Mass(10));
    dynoDustEmitter.addInitializer(new Nebula.Radius(.1));
    dynoDustEmitter.addInitializer(new Nebula.Life(1,3));

    let ddustGeometry = new THREE.BoxGeometry(.1, .1, .1);
    let ddustMaterial = new THREE.MeshLambertMaterial({
        color: '#E7B251', //E7B251
    });
    dynoDustEmitter.addInitializer(new Nebula.Body(new THREE.Mesh(ddustGeometry, ddustMaterial)));

    let ddustRadVelB = new Nebula.RadialVelocity(spd, new Nebula.Vector3D(0, 15, 20), 40);
    dynoDustEmitter.addInitializer(ddustRadVelB);

    dynoDustEmitter.addBehaviour(new Nebula.Rotate('random', 'random'));
    dynoDustEmitter.addBehaviour(new Nebula.Scale(2, 0.1));
    // dynoDustEmitter.addBehaviour(new Nebula.Attraction(new Nebula.Vector3D(0, 2, 30), 1, 10));
    //Gravity
    // dynoDustEmitter.addBehaviour(new Nebula.Gravity(-0.1));


    let ddZone = new Nebula.BoxZone(3, 2, 25);
    //ddZone.friction = 0.95;
    ddZone.max = 10;
    dynoDustEmitter.addBehaviour(new Nebula.CrossZone(ddZone, 'bound'));

    function setP(x,y,z) {
        dynoDustEmitter.position.x = x;
        dynoDustEmitter.position.y = y;
        dynoDustEmitter.position.z = z;
        ddZone.x = x;
        ddZone.y = y;
        ddZone.z = z;
    }

    setP(0, -1.1, 15.5);

    dynoDustEmitter.emit();

    return dynoDustEmitter;
}

let dynoDustEmitter = nebulaCreateDynoDustEmitter(4);

// Nebula.Debug.drawZone(THREE, nebulaSystem, scene, ddZone);
// Nebula.Debug.drawEmitter(THREE, nebulaSystem, scene, dynoDustEmitter);

nebulaSystem.addEmitter(dynoDustEmitter);
nebulaSystem.addRenderer(new Nebula.MeshRenderer(scene, THREE));