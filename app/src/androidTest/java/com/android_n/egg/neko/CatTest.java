package com.android_n.egg.neko;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class CatTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void create() {
        for (int i = 0; i < 5; i++) {
            System.out.println(new Random().nextInt());
//            Cat cat = Cat.create(context);
//            System.out.println(cat.getSeed());
//            System.out.println(cat.getName());
        }
    }

    @Test
    public void preCreate() {
        final PrefState prefs = new PrefState(context);
//        int food = prefs.getFoodState();
        int food = 1;
        Log.i("CatTest", "food: " + food);
        final Random rng = new Random();
        if (rng.nextFloat() <= 1.0f) {
            Cat cat;
            List<Cat> cats = prefs.getCats();
            final int[] probs = context.getResources().getIntArray(com.android_n.egg.R.array.food_new_cat_prob);
            final float new_cat_prob = (float) ((food < probs.length) ? probs[food] : 50) / 100f;
            Log.i("CatTest", "new_cat_prob: " + new_cat_prob);
            if (cats.size() == 0 || rng.nextFloat() <= new_cat_prob) {
                cat = Cat.create(context);
                prefs.addCat(cat);
                cat.logAdd(context);
                Log.v("CatTest", "A new cat is here: " + cat.getName());
            } else {
                cat = cats.get(rng.nextInt(cats.size()));
                Log.v("CatTest", "A cat has returned: " + cat.getName());
            }
        }
    }
}