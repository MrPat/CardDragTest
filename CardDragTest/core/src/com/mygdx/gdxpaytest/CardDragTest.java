package com.mygdx.gdxpaytest;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class CardDragTest extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Card img;
	boolean isDragging = false;
	boolean didDrag = false;
	Card draggee;
	Stage stage;
	Skin skin;
	Table screen;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Card(new Texture("badlogic.jpg"));
		stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		skin = new Skin(Gdx.files.internal("uiskin.json"));

		// Use a Scene2D table as our screen
		screen = new Table();
		screen.setWidth(Gdx.graphics.getWidth());
		screen.setHeight(Gdx.graphics.getHeight());

		// Add the screen to the stage and the card to the screen
		stage.addActor(screen);
		screen.addActor(img);

		// Set the card up to be about the same size as a card in my app
		img.setWidth(Gdx.graphics.getWidth() / 9f);
		img.setHeight(img.getWidth() * 1.3f);
		img.setX(Gdx.graphics.getWidth() / 2);
		img.setY(Gdx.graphics.getHeight() / 2);

		screen.top();

		// Make the card so it can be dragged
		makeCardDraggable(img);

		// Make sure we can process input from multiple sources. This is very important
		InputMultiplexer mux = new InputMultiplexer();
		mux.addProcessor(this);
		mux.addProcessor(stage);
		Gdx.input.setInputProcessor(mux);
	}

	@Override
	public void render () {
		// Check whether a card is being dragged
		if (isDragging && draggee != null) {
			// Only accept the first touch
			if (Gdx.input.isTouched(0)) {
				dragCard();
			} else {
				dropCard();
			}
		}

		Gdx.gl.glClearColor(.8f, .8f, .8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.act();
		stage.draw();
	}

	public void log(String message) {
		Gdx.app.log("CardClickTest", message);
	}

	public void makeCardDraggable(final Card card) {
		// Takes a PlayCard and makes it to where the user can drag it around the screen
		final ArrayList<Float> coords = new ArrayList<Float>();

		ClickListener click = new ClickListener() {

			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				// Only accept the first finger as a touch
				if (pointer == 0) {
					didDrag = false;
					// Save the touch coords and touch time, for magnitude-based or time-based dragging
					// Not using either of these right now because they only work with some devices
					coords.clear();
					coords.add(x);
					coords.add(y);
					return true;
				}
				return super.touchDown(event, x, y, pointer, button);
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y,
					int pointer) {					
				// Can't drag while dragging
				if (isDragging || pointer != 0) return;

				// Right now magnitude isn't being used because it causes bad performance
				//double magnitude = Math.sqrt(Math.pow((double) (x - coords.get(0)) , 2) + Math.sqrt(Math.pow((double) (y - coords.get(1)) , 2)));
				//if (pointer == 0 && magnitude > MINIMUM_DRAG_DISTANCE) {
				//if (pointer == 0 && System.currentTimeMillis() - dragTime > MINIMUM_DRAG_TIME) {
				if (pointer == 0) {
					isDragging = true;
					didDrag = true;

					// draggee is how we keep up with the card being dragged
					draggee = card;

					// Center the card at the touch
					Vector2 newCoords = new Vector2(Gdx.input.getX(0), Gdx.graphics.getHeight() - Gdx.input.getY(0));
					newCoords = screen.stageToLocalCoordinates(newCoords);
					draggee.setPosition(newCoords.x - draggee.getWidth() / 2f, newCoords.y - draggee.getHeight() / 2f);
				} 

				super.touchDragged(event, x, y, pointer);
			}

			@Override
			public void touchUp(InputEvent event, float x, float y,
					int pointer, int button) {
				log("Touch up at " + x + "," + y);
				if (didDrag) return;
				RotateByAction rotate = new RotateByAction();
				rotate.setAmount(360);
				rotate.setDuration(.5f);
				card.setOrigin(Align.center);
				card.addAction(rotate);
				super.touchUp(event, x, y, pointer, button);
			}

		};

		card.addListener(click);
	}

	public void dragCard() {
		// Position the card at your fingertip
		Vector2 newCoords = new Vector2(Gdx.input.getX(0), Gdx.graphics.getHeight() - Gdx.input.getY(0));
		newCoords = screen.stageToLocalCoordinates(newCoords);
		draggee.setPosition(newCoords.x - draggee.getWidth() / 2f, newCoords.y - draggee.getHeight() / 2f);
	}

	public void dropCard() {
		isDragging = false; // No longer dragging a card now
		log("Dropped card");
		draggee = null;
	}

	public class Card extends Image {
		public Card(Texture texture) {
			super(texture);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
}
