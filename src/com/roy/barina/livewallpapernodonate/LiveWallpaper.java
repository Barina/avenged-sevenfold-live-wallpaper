package com.roy.barina.livewallpapernodonate;

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
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.extension.ui.livewallpaper.BaseLiveWallpaperService;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class LiveWallpaper extends BaseLiveWallpaperService implements SharedPreferences.OnSharedPreferenceChangeListener, SettingsConstants//, IOffsetsChanged
{
	public static final String SHARED_PREFS_NAME = "livewallpapertemplatesettings";
	public static final int LAYER_BG = 0, LAYER_DEATHBAT = 1, LAYER_TITLE = 2;
	private static Camera mCamera;
	private static Texture texture, titleTexture, bgTexture;
	private static TextureRegion skullRegion, leftWingRegion, rightWingRegion, jawRegion, /*titleRegion,*/ bgRegion;
	private static Sprite skull, jaw, rightWing, leftWing,/* titleSprite,*/ bgSprite;
	private static ChangeableText titleText;
	private static Font titleFont;
	private static Context context;
	private static Scene scene;
	private static org.anddev.andengine.engine.Engine engine;

//	@Override
//	public Engine onCreateEngine()
//	{
//		return new MyBaseWallpaperGLEngine(this);
//	}

	@Override
	public org.anddev.andengine.engine.Engine onLoadEngine()
	{		
		context = getBaseContext();
		mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		mCamera.setCenter(0, CAMERA_HEIGHT * 0.5f);
		return new org.anddev.andengine.engine.Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT, new FillResolutionPolicy(), mCamera));
	}

	@Override
	public synchronized void onLoadResources()
	{
		engine = getEngine();
		Settings.loadContext(getBaseContext());
		// Set the Base Texture Path
		TextureRegionFactory.setAssetBasePath("gfx/");
		bgTexture = new Texture(1024, 1024, TextureOptions.BILINEAR);
		titleTexture = new Texture(1024, 256, TextureOptions.REPEATING);
		bgRegion = TextureRegionFactory.createFromAsset(bgTexture, this, "middle_bg.png", 0, 0);
		titleFont = FontFactory.createFromAsset(titleTexture, context, "font/CHILLER.TTF", 72, true, Color.WHITE);
		engine.getTextureManager().loadTextures(bgTexture, titleTexture);
		engine.getFontManager().loadFont(titleFont);
		bgSprite = new Sprite(-(bgRegion.getWidth() * 0.5f), (CAMERA_HEIGHT * 0.5f - bgRegion.getHeight() * 0.5f) + 250, bgRegion);
		titleText = new ChangeableText(0, 0, titleFont, "", MAX_TITLE_LENGTH);
		initTextureRegions(Settings.getSettingAsBoolean(IS_BLACK_SETTING));
	}

	@Override
	public synchronized Scene onLoadScene()
	{
		scene = new Scene(3);
		setSceneBGColor(Settings.getSettingAsBoolean(IS_BLACK_SETTING));
		scene.getLayer(LAYER_BG).addEntity(bgSprite);
		scene.getLayer(LAYER_TITLE).addEntity(titleText);
		addSpritesToScene();
		return scene;
	}

	@Override
	public void onLoadComplete()
	{
		pauseScene(Settings.getSettingAsBoolean(IS_PAUSED_SETTING));
	}

//	@Override
//	public void offsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset)
//	{
//		float screensCount = (1 / xOffsetStep) + 1;
//		if(mCamera != null)
//			mCamera.setCenter(((CAMERA_WIDTH * (screensCount - 1)) * xOffset) - CAMERA_WIDTH, mCamera.getCenterY());
//	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences pSharedPrefs, String pKey)
	{}

	@Override
	protected void onPause()
	{
		pauseScene(true);
		super.onPause();
	}
	
	@Override
	protected void onResume()
	{
		pauseScene(Settings.getSettingAsBoolean(IS_PAUSED_SETTING));
		super.onResume();
	}
	
	private synchronized static void initTextureRegions(Boolean isBlack)
	{
		if(texture != null)
			engine.getTextureManager().unloadTexture(texture);
		texture = new Texture(256, 512, TextureOptions.REPEATING);
//		titleTexture = new Texture(256, 32, TextureOptions.REPEATING);
		String color = isBlack ? "black_" : "white_";
		skullRegion = TextureRegionFactory.createFromAsset(texture, context, color + "skull.png", 1, 1);
		leftWingRegion = TextureRegionFactory.createFromAsset(texture, context, color + "left_wing.png", 1, 157);
		rightWingRegion = TextureRegionFactory.createFromAsset(texture, context, color + "right_wing.png", 1, 260);
		jawRegion = TextureRegionFactory.createFromAsset(texture, context, color + "jaw.png", 1, 361);
//		titleRegion = TextureRegionFactory.createFromAsset(titleTexture, context, color + "title.png", 0, 0);
		engine.getTextureManager().loadTexture(texture);
		final int logoTopDistance = Settings.getSettingAsInt(LOGO_TOP_DISTANCE_SETTING),/* titleTopDistance = Settings.getSettingAsInt(TITLE_TOP_DISTANCE_SETTING),*/ 
		logoCenterDistance = Settings.getSettingAsInt(LOGO_CENTER_DISTANCE_SETTING);//, titleCenterDistance = Settings.getSettingAsInt(TITLE_CENTER_DISTANCE_SETTING);
		skull = new Sprite(-(skullRegion.getWidth() * 0.5f) + logoCenterDistance, logoTopDistance, skullRegion);
		rightWing = new Sprite(-(rightWingRegion.getWidth() * 0.5f) + rightWingRegion.getWidth() * 0.8f + logoCenterDistance, logoTopDistance + 20, rightWingRegion);
		rightWing.setRotationCenterX(0);
		leftWing = new Sprite(-(leftWingRegion.getWidth() * 0.5f) - leftWingRegion.getWidth() * 0.8f + logoCenterDistance, rightWing.getBaseY(), leftWingRegion);
		leftWing.setRotationCenterX(leftWing.getWidth());
		jaw = new Sprite(-(jawRegion.getWidth() * 0.5f) + logoCenterDistance, logoTopDistance + 120, jawRegion);

		updateTitleText();
		titleText.setVisible(Settings.getSettingAsBoolean(DRAW_TITLE_SETTING));
		
//		titleSprite = new Sprite(-(titleRegion.getWidth() * 0.5f) + titleCenterDistance, titleTopDistance, titleRegion);
//		titleSprite.addShapeModifier(new SequenceModifier(new IShapeModifier.IShapeModifierListener()
//		{
//			@Override
//			public void onModifierFinished(IShapeModifier modifier, IShape shape)
//			{
//				modifier.reset();
//			}
//		}, new RotationModifier(2.5f, -1.5f, 1.5f), new RotationModifier(2.8f, 1.5f, -1.5f)));
//		titleSprite.addShapeModifier(new SequenceModifier(new IShapeModifier.IShapeModifierListener()
//		{
//			@Override
//			public void onModifierFinished(IShapeModifier modifier, IShape shape)
//			{
//				modifier.reset();
//			}
//		}, new MoveModifier(2, titleSprite.getX(), titleSprite.getX(), titleSprite.getY(), titleSprite.getY() + 20), new MoveModifier(2, titleSprite.getX(), titleSprite.getX(),
//				titleSprite.getY() + 20, titleSprite.getY())));
//		titleSprite.setVisible(Settings.getSettingAsBoolean(DRAW_TITLE_SETTING));
		final SequenceModifier skullSequenceModifier = new SequenceModifier(new MoveModifier(2, skull.getX(), skull.getX(), skull.getY(), skull.getY() + 50), new MoveModifier(
				0.5f, skull.getX(), skull.getX(), skull.getY() + 50, skull.getY()));
		skullSequenceModifier.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				skullSequenceModifier.reset();
			}
		});
		skull.addShapeModifier(skullSequenceModifier);
		final SequenceModifier jawSequenceModifier = new SequenceModifier(new MoveModifier(2, jaw.getX(), jaw.getX(), jaw.getY(), jaw.getY() + 70), new MoveModifier(0.5f,
				jaw.getX(), jaw.getX(), jaw.getY() + 70, jaw.getY()));
		jawSequenceModifier.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				jawSequenceModifier.reset();
			}
		});
		jaw.addShapeModifier(jawSequenceModifier);
		final SequenceModifier rightWingSequenceModifier1 = new SequenceModifier(new RotationModifier(2, 80f, 0f), new RotationModifier(0.5f, 0f, 80f));
		rightWingSequenceModifier1.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				rightWingSequenceModifier1.reset();
			}
		});
		final SequenceModifier rightWingSequenceModifier2 = new SequenceModifier(new MoveModifier(2, rightWing.getX(), rightWing.getX(), rightWing.getY(), rightWing.getY() + 50),
				new MoveModifier(0.5f, rightWing.getX(), rightWing.getX(), rightWing.getY() + 50, rightWing.getY()));
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
		final SequenceModifier leftWingSequenceModifier1 = new SequenceModifier(new RotationModifier(2, -80f, 0f), new RotationModifier(0.5f, 0f, -80f));
		leftWingSequenceModifier1.setShapeModifierListener(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier arg0, IShape arg1)
			{
				leftWingSequenceModifier1.reset();
			}
		});
		final SequenceModifier leftWingSequenceModifier2 = new SequenceModifier(new MoveModifier(2, leftWing.getX(), leftWing.getX(), leftWing.getY(), leftWing.getY() + 50),
				new MoveModifier(0.5f, leftWing.getX(), leftWing.getX(), leftWing.getY() + 50, leftWing.getY()));
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
		updateDistances();
	}

	public static void updateTitleText()
	{
		final int  titleTopDistance = Settings.getSettingAsInt(TITLE_TOP_DISTANCE_SETTING), titleCenterDistance = Settings.getSettingAsInt(TITLE_CENTER_DISTANCE_SETTING);
		titleText.clearShapeModifiers();
		titleText.setText(Settings.getSettingAsString(TITLE_STRING_SETTING));
		if(Settings.getSettingAsBoolean(IS_BLACK_SETTING))
			titleText.setColor(0, 0, 0);
		else
			titleText.setColor(1, 1, 1);
		titleText.setPosition(-(titleText.getWidth() * 0.5f) + titleCenterDistance, titleTopDistance);
		titleText.addShapeModifier(new SequenceModifier(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier modifier, IShape shape)
			{
				modifier.reset();
			}
		}, new RotationModifier(2.5f, -1.5f, 1.5f), new RotationModifier(2.8f, 1.5f, -1.5f)));
		titleText.addShapeModifier(new SequenceModifier(new IShapeModifier.IShapeModifierListener()
		{
			@Override
			public void onModifierFinished(IShapeModifier modifier, IShape shape)
			{
				modifier.reset();
			}
		}, new MoveModifier(2, titleText.getX(), titleText.getX(), titleText.getY(), titleText.getY() + 20), new MoveModifier(2, titleText.getX(), titleText.getX(), titleText
				.getY() + 20, titleText.getY())));
	}

	private synchronized static void updateDistances()
	{
		if(skull == null || rightWing == null || leftWing == null || jaw == null)// || titleSprite == null)
			return;
		final int logoDistance = Settings.getSettingAsInt(LOGO_TOP_DISTANCE_SETTING), titleDistance = Settings.getSettingAsInt(TITLE_TOP_DISTANCE_SETTING);
		skull.setPosition(skull.getX(), logoDistance);
		rightWing.setPosition(rightWing.getX(), logoDistance + 20);
		leftWing.setPosition(leftWing.getX(), logoDistance + 20);
		jaw.setPosition(jaw.getX(), logoDistance + 120);
//		titleSprite.setPosition(titleSprite.getX(), titleDistance);
		titleText.setPosition(titleText.getX(), titleDistance);
	}

	public synchronized static void pauseScene(boolean pause)
	{
		if(scene != null)
			scene.setIgnoreUpdate(pause);
	}

	private synchronized static void addSpritesToScene()
	{
		if(scene == null)
			return;
		scene.getLayer(LAYER_DEATHBAT).addEntity(rightWing);
		scene.getLayer(LAYER_DEATHBAT).addEntity(leftWing);
		scene.getLayer(LAYER_DEATHBAT).addEntity(jaw);
		scene.getLayer(LAYER_DEATHBAT).addEntity(skull);
//		scene.getLayer(LAYER_TITLE).addEntity(titleSprite);
	}

	private synchronized static void setSceneBGColor(boolean isBlack)
	{
		if(scene != null)
			if(isBlack)
				scene.setBackground(new ColorBackground(1.0f, 1.0f, 1.0f));
			else
				scene.setBackground(new ColorBackground(0.0f, 0.0f, 0.0f));
	}

	public synchronized static void drawTitle(boolean draw)
	{
//		if(titleSprite != null)
//			titleSprite.setVisible(draw);
		if(titleText != null)
			titleText.setVisible(draw);
	}

	public synchronized static void changeColor(boolean toBlack)
	{
		if(scene == null)
			return;
		scene.getLayer(LAYER_DEATHBAT).removeEntity(rightWing);
		scene.getLayer(LAYER_DEATHBAT).removeEntity(leftWing);
		scene.getLayer(LAYER_DEATHBAT).removeEntity(jaw);
		scene.getLayer(LAYER_DEATHBAT).removeEntity(skull);
//		scene.getLayer(LAYER_TITLE).removeEntity(titleSprite);
		initTextureRegions(toBlack);
		updateDistances();
		setSceneBGColor(toBlack);
		addSpritesToScene();
	}

//	protected class MyBaseWallpaperGLEngine extends GLEngine
//	{
//		private Renderer mRenderer;
//		private IOffsetsChanged mOffsetsChangedListener = null;
//
//		public MyBaseWallpaperGLEngine(IOffsetsChanged pOffsetsChangedListener)
//		{
//			this.setEGLConfigChooser(false);
//			this.mRenderer = new RenderSurfaceView.Renderer(LiveWallpaper.this.mEngine);
//			this.setRenderer(this.mRenderer);
//			this.setRenderMode(RENDERMODE_CONTINUOUSLY);
//			this.mOffsetsChangedListener = pOffsetsChangedListener;
//		}
//
//		@Override
//		public Bundle onCommand(final String pAction, final int pX, final int pY, final int pZ, final Bundle pExtras, final boolean pResultRequested)
//		{
//			if(pAction.equals(WallpaperManager.COMMAND_TAP))
//				LiveWallpaper.this.onTap(pX, pY);
//			else
//				if(pAction.equals(WallpaperManager.COMMAND_DROP))
//					LiveWallpaper.this.onDrop(pX, pY);
//			return super.onCommand(pAction, pX, pY, pZ, pExtras, pResultRequested);
//		}
//
//		@Override
//		public void onResume()
//		{
//			super.onResume();
//			LiveWallpaper.this.getEngine().onResume();
//			LiveWallpaper.this.onResume();
//		}
//
//		@Override
//		public void onPause()
//		{
//			super.onPause();
//			LiveWallpaper.this.getEngine().onPause();
//			LiveWallpaper.this.onPause();
//		}
//
//		@Override
//		public void onDestroy()
//		{
//			super.onDestroy();
//			if(this.mRenderer != null)
//			{ // mRenderer.release();
//			}
//			this.mRenderer = null;
//		}
//
//		@Override
//		public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset)
//		{
//			super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
//			if(this.mOffsetsChangedListener != null)
//				this.mOffsetsChangedListener.offsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixelOffset, yPixelOffset);
//		}
//	}
}