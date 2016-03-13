package mapping;

import engine.Entity;
import engine.IUpdatable;

import java.util.ArrayList;

import utils.Vector2;

public class GameMap implements IUpdatable {
	public enum Layer {
		ZERO("zero"),
		BACKGROUND("back"),
		FOREGROUND("front"),
		MIDDLE("middle"),
		INVALID(null);
		
		private String layerName;
		
		private Layer(String name){
			layerName = name;
		}
		
		public String getLayerName(){
			return layerName;
		}
	}
	
	private ArrayList<Entity> mapEntities = new ArrayList<Entity>();
	private ArrayList<Entity> zeroEntities = new ArrayList<Entity>();
	private ArrayList<Entity> backEntities = new ArrayList<Entity>();
	private ArrayList<Entity> middleEntities = new ArrayList<Entity>();
	private ArrayList<Entity> frontEntities = new ArrayList<Entity>();
	private String mapPath;
	
	public GameMap(String mapPath){
		this.mapPath = mapPath;
	}
	
	public void addEntity(Layer layer, Entity e){
		if (e == null){
			return;
		}
		
		switch (layer){
			case ZERO:{
				zeroEntities.add(e);
				break;
			}
			case BACKGROUND:{
				backEntities.add(e);
				break;
			}
			case FOREGROUND:{
				frontEntities.add(e);
				break;
			}
			case MIDDLE:{
				middleEntities.add(e);
				break;
			}
			default:{
				break;
			}
		}
		
		mapEntities.add(e);
	}

	public void pan(Layer layer, Vector2 offset){
		switch (layer){
			case ZERO:{
				pan(zeroEntities, offset);
				break;
			}
			case BACKGROUND:{
				pan(backEntities, offset);
				break;
			}
			case FOREGROUND:{
				pan(frontEntities, offset);
				break;
			}
			case MIDDLE:{
				pan(middleEntities, offset);
				break;
			}
			default:{
				break;
			}
		}
	}
	
	private void pan(ArrayList<Entity> entityList, Vector2 offset){
		for (Entity e : entityList){
			e.setPosition(e.getPosition().add(offset));
		}
	}
	
	public void renderLayer(Layer layer){
		switch (layer){
			case ZERO:{
				render(zeroEntities);
				break;
			}
			case BACKGROUND:{
				render(backEntities);
				break;
			}
			case FOREGROUND:{
				render(frontEntities);
				break;
			}
			case MIDDLE:{
				render(middleEntities);
				break;
			}
			default:{
				break;
			}
		}
	}
	
	private void render(ArrayList<Entity> entityList){
		for (Entity e : entityList){
			e.render();
		}
	}

	@Override
	public void update(float deltaTime) {
		for (Entity e : mapEntities){
			e.update(deltaTime);
		}
	}
}
