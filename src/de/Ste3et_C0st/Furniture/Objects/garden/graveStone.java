package de.Ste3et_C0st.Furniture.Objects.garden;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import de.Ste3et_C0st.Furniture.Main.FurnitureHook;
import de.Ste3et_C0st.FurnitureLib.Utilitis.LocationUtil;
import de.Ste3et_C0st.FurnitureLib.main.Furniture;
import de.Ste3et_C0st.FurnitureLib.main.ObjectID;
import de.Ste3et_C0st.FurnitureLib.main.Type;
import de.Ste3et_C0st.FurnitureLib.main.Type.SQLAction;

public class graveStone extends Furniture{

	private Location signLoc;
	private Block sign;
	private String[] lines = new String[4];
	
	public graveStone(ObjectID id) {
		super(id);
		setBlock();
	}
	
	private void setBlock(){
		this.signLoc = getObjID().getBlockList().stream().filter(b -> b.getBlock().getType().name().contains("SIGN")).findFirst().orElse(null);
		if(this.signLoc != null) this.sign = this.signLoc.getBlock();
		this.lines = getText();
	}
	
	@Override
	public void onBreak(Player player) {
		if(getObjID() == null) return;
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)) return;
		if(player == null) return;
		if(canBuild(player)) {
			this.destroy(player);
			if(sign!=null){
				sign.setType(Material.AIR);
			}
		}
	}
	
	@Override
	public void onClick(Player player){
		if(getObjID() == null) return;
		if(getObjID().getSQLAction().equals(SQLAction.REMOVE)) return;
		if(player == null) return;
		if(canBuild(player)) {
			ItemStack is = player.getInventory().getItemInMainHand();
			if (is == null) return;
			if (!is.getType().equals(Material.WRITTEN_BOOK)) return;
			readFromBook(is);
		}
	}

	public void resetSign(){
		Bukkit.getScheduler().scheduleSyncDelayedTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				sign = getLutil().setSign(getBlockFace(), signLoc);
				placetext();
			}
		});
	}
	
	public Location getSignLocation(){return this.signLoc;}
	
	public void removeSign(){
		if(sign!=null){
			sign.setType(Material.AIR);
			sign = null;
			getManager().remove(getObjID());
			delete();
		}
	}
	
	public void readFromBook(ItemStack is){
		BookMeta bm = (BookMeta) is.getItemMeta();
		if(bm == null){return;}
		String side = bm.getPage(1);
		if(side==null){return;}
		String lines[] = side.split("\\r?\\n");
		
		Integer line = 0;
		for(String s : lines){
			if(s!=null && line<=3){
				Integer i = 15;
				if(s.length()>=15){i=15;}else{i=s.length();}
				String a = s.substring(0, i);
				if(a!=null){
					a = ChatColor.translateAlternateColorCodes('&', a);
					setText(line, a);
				}
				line++;
			}
		}
		
		if(line!=3){
			for(int i = line; i<=3; i++){
				setText(i, "");
			}
		}
		return;
	}
	
	public void placetext(){
		if ((this.sign.getState() instanceof Sign) && lines != null){
			Sign sign = (Sign) this.sign.getState();
			Integer i = 0;
			for(String s : lines){
				if(i>3){break;}
				sign.setLine(i, s);
				i++;
			}
			sign.update(true, false);
		}
	}
	
	public String[] getText(){
		if(sign==null || !sign.getType().name().contains("SIGN")){return null;}
		Sign sign = (Sign) this.sign.getState();
		return sign.getLines();
	}
	
	public void setText(Integer line, String text){
		if(line==null || text == null){return;}
		if(sign==null || !sign.getType().name().contains("SIGN")){return;}
		Sign sign = (Sign) this.sign.getState();
		sign.setLine(line, text);
		sign.update(true, false);
		lines[line] = text;
	}

	@Override
	public void spawn(Location location) {}
}
