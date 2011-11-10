package com.mimminito.livewallpapertemplate;

import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.shape.IShape;
import org.anddev.andengine.entity.shape.modifier.IShapeModifier;
import org.anddev.andengine.entity.shape.modifier.MoveModifier;
import org.anddev.andengine.entity.shape.modifier.RotationModifier;
import org.anddev.andengine.entity.shape.modifier.SequenceModifier;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import android.content.SharedPreferences;

public class LiveWallpaperTemplate extends BaseLiveWallpaperService implements SharedPreferences.OnSharedPreferenceChangeListener
{
	// ===========================================================
	// Constants
	// ===========================================================
	public static final String SHARED_PREFS_NAME = "livewallpapertemplatesettings";
	
	// Camera Constants
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 720;
	
	// ===========================================================
	// Fields
	// ===========================================================
	private Sprite skull, jaw, rightWing, leftWing;
	private TextureRegion skullRegion,leftWingRegion,rightWingRegion,jawRegion;
	
	// Shared Preferences
	private SharedPreferences mSharedPreferences;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================
	
	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================	
	@Override
	public org.anddev.andengine.engine.Engine onLoadEngine()
	{
		Camera mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new org.anddev.andengine.engine.Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, new FillResolutionPolicy(), mCamera));
	}

	@Override
	public void onLoadResources()
	{
		// Set the Base Texture Path
		TextureRegionFactory.setAssetBasePath("gfx/");
		Texture texture = new Texture(256, 512);
		skullRegion = TextureRegionFactory.createFromAsset(texture, getBaseContext(), "skull.png", 1, 1);
		leftWingRegion = TextureRegionFactory.createFromAsset(texture, getBaseContext(), "left_wing.png", 1, 157);
		rightWingRegion = TextureRegionFactory.createFromAsset(texture, getBaseContext(), "right_wing.png", 1, 260);
		jawRegion = TextureRegionFactory.createFromAsset(texture, getBaseContext(), "jaw.png", 1, 361);
		getEngine().getTextureManager().loadTexture(texture);
	}

	@Override
	public Scene onLoadScene()
	{
		final Scene scene = new Scene(1);
		scene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));
		skull = new Sprite(CAMERA_WIDTH * 0.5f - skullRegion.getWidth() * 0.5f, CAMERA_HEIGHT * 0.5f - skullRegion.getHeight() * 0.5f, skullRegion);
		rightWing = new Sprite((CAMERA_WIDTH * 0.5f - rightWingRegion.getWidth() * 0.5f) + rightWingRegion.getWidth() * 0.8f, skull.getBaseY() + 20, rightWingRegion);
		rightWing.setRotationCenterX(0);
		leftWing = new Sprite((CAMERA_WIDTH * 0.5f - leftWingRegion.getWidth() * 0.5f) - leftWingRegion.getWidth() * 0.8f, rightWing.getBaseY(), leftWingRegion);
		leftWing.setRotationCenterX(leftWing.getWidth());
		jaw = new Sprite(CAMERA_WIDTH * 0.5f - jawRegion.getWidth() * 0.5f, (CAMERA_HEIGHT * 0.5f - jawRegion.getHeight() * 0.5f) + 80, jawRegion);

		final SequenceModifier skullSequenceModifier = new SequenceModifier(new MoveModifier(2, skull.getX(), skull.getX(), skull.getY(), skull.getY() + 50),
				new MoveModifier(0.5f, skull.getX(), skull.getX(), skull.getY()+50, skull.getY()));
		skullSequenceModifier.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				skullSequenceModifier.reset();
			}
		});		
		skull.addShapeModifier(skullSequenceModifier);
		
		final SequenceModifier jawSequenceModifier = new SequenceModifier(new MoveModifier(2, jaw.getX(), jaw.getX(), jaw.getY(), jaw.getY()+70),
				new MoveModifier(0.5f, jaw.getX(), jaw.getX(), jaw.getY()+70, jaw.getY())); 
		jawSequenceModifier.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				jawSequenceModifier.reset();
			}
		});
		jaw.addShapeModifier(jawSequenceModifier);
		
		final SequenceModifier rightWingSequenceModifier1 = new SequenceModifier(new RotationModifier(2, 80f, 0f),new RotationModifier(0.5f, 0f, 80f));
		rightWingSequenceModifier1.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				rightWingSequenceModifier1.reset();
			}
		});
		final SequenceModifier rightWingSequenceModifier2 = new SequenceModifier(new MoveModifier(2, rightWing.getX(), rightWing.getX(), rightWing.getY(), rightWing.getY()+50),
				new MoveModifier(0.5f, rightWing.getX(), rightWing.getX(), rightWing.getY()+50, rightWing.getY()));
		rightWingSequenceModifier2.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				rightWingSequenceModifier2.reset();
			}
		});
		rightWing.addShapeModifier(rightWingSequenceModifier1);
		rightWing.addShapeModifier(rightWingSequenceModifier2);
		
		final SequenceModifier leftWingSequenceModifier1 = new SequenceModifier(new RotationModifier(2, -80f, 0f),new RotationModifier(0.5f, 0f, -80f));
		leftWingSequenceModifier1.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				leftWingSequenceModifier1.reset();
			}
		});
		final SequenceModifier leftWingSequenceModifier2 = new SequenceModifier(new MoveModifier(2, leftWing.getX(), leftWing.getX(), leftWing.getY(), leftWing.getY()+50),
				new MoveModifier(0.5f, leftWing.getX(), leftWing.getX(), leftWing.getY()+50, leftWing.getY()));
		leftWingSequenceModifier2.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				leftWingSequenceModifier2.reset();
			}
		});
		leftWing.addShapeModifier(leftWingSequenceModifier1);
		leftWing.addShapeModifier(leftWingSequenceModifier2);
		return scene;
	}

	@Override
	public void onLoadComplete()
	{
		getEngine().getScene().getTopLayer().addEntity(rightWing);
		getEngine().getScene().getTopLayer().addEntity(leftWing);
		getEngine().getScene().getTopLayer().addEntity(skull);
		getEngine().getScene().getTopLayer().addEntity(jaw);
	}

	@Override
	protected void onTap(final int pX, final int pY)
	{}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pSharedPrefs, String pKey)
	{}
	
	// ===========================================================
	// Methods
	// ===========================================================
	
	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}