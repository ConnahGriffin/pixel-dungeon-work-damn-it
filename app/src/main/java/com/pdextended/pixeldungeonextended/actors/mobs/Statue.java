/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.pdextended.pixeldungeonextended.actors.mobs;

import java.util.HashSet;

import com.pdextended.pixeldungeonextended.Dungeon;
import com.pdextended.pixeldungeonextended.Journal;
import com.pdextended.pixeldungeonextended.actors.Char;
import com.pdextended.pixeldungeonextended.actors.blobs.ToxicGas;
import com.pdextended.pixeldungeonextended.actors.buffs.Poison;
import com.pdextended.pixeldungeonextended.items.Generator;
import com.pdextended.pixeldungeonextended.items.scrolls.ScrollOfPsionicBlast;
import com.pdextended.pixeldungeonextended.items.weapon.Weapon;
import com.pdextended.pixeldungeonextended.items.weapon.enchantments.Death;
import com.pdextended.pixeldungeonextended.items.weapon.enchantments.Leech;
import com.pdextended.pixeldungeonextended.items.weapon.melee.MeleeWeapon;
import com.pdextended.pixeldungeonextended.sprites.StatueSprite;
import com.pdextended.utils.Bundle;
import com.pdextended.utils.Random;

public class Statue extends Mob {
	
	{
		name = "animated statue";
		spriteClass = StatueSprite.class;

		EXP = 0;
		state = PASSIVE;
	}
	
	private Weapon weapon;
	
	public Statue() {
		super();
		
		do {
			weapon = (Weapon)Generator.random( Generator.Category.WEAPON );
		} while (!(weapon instanceof MeleeWeapon) || weapon.level() < 0);
		
		weapon.identify();
		weapon.enchant();
		
		HP = HT = 15 + Dungeon.depth * 5;
		defenseSkill = 4 + Dungeon.depth;
	}
	
	private static final String WEAPON	= "weapon";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( WEAPON, weapon );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		weapon = (Weapon)bundle.get( WEAPON );
	}
	
	@Override
	protected boolean act() {
		if (Dungeon.visible[pos]) {
			Journal.add( Journal.Feature.STATUE );
		}
		return super.act();
	}
	
	@Override
	public int damageRoll() {
		return Random.NormalIntRange( weapon.min(), weapon.max() );
	}
	
	@Override
	public int attackSkill( Char target ) {
		return (int)((9 + Dungeon.depth) * weapon.ACU);
	}
	
	@Override
	protected float attackDelay() {
		return weapon.DLY;
	}
	
	@Override
	public int dr() {
		return Dungeon.depth;
	}
	
	@Override
	public void damage( int dmg, Object src ) {

		if (state == PASSIVE) {
			state = HUNTING;
		}
		
		super.damage( dmg, src );
	}
	
	@Override
	public int attackProc( Char enemy, int damage ) {
		weapon.proc( this, enemy, damage );
		return damage;
	}
	
	@Override
	public void beckon( int cell ) {
		// Do nothing
	}
	
	@Override
	public void die( Object cause ) {
		Dungeon.level.drop( weapon, pos ).sprite.drop();
		super.die( cause );
	}
	
	@Override
	public void destroy() {
		Journal.remove( Journal.Feature.STATUE );
		super.destroy();
	}
	
	@Override
	public boolean reset() {
		state = PASSIVE;
		return true;
	}

	@Override
	public String description() {
		return
			"You would think that it's just another ugly statue of this dungeon, but its red glowing eyes give itself away. " +
			"While the statue itself is made of stone, the _" + weapon.name() + "_, it's wielding, looks real.";
	}
	
	private static final HashSet<Class<?>> RESISTANCES = new HashSet<>();
	private static final HashSet<Class<?>> IMMUNITIES = new HashSet<>();
	static {
		RESISTANCES.add( ToxicGas.class );
		RESISTANCES.add( Poison.class );
		RESISTANCES.add( Death.class );
		RESISTANCES.add( ScrollOfPsionicBlast.class );
		IMMUNITIES.add( Leech.class );
	}
	
	@Override
	public HashSet<Class<?>> resistances() {
		return RESISTANCES;
	}
	
	@Override
	public HashSet<Class<?>> immunities() {
		return IMMUNITIES;
	}
}
