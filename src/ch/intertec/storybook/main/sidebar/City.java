/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.intertec.storybook.main.sidebar;

import ch.intertec.storybook.model.Location;

public class City {

	private String city;
	private Country country;

	public City(String city) {
		this(city, null);
	}

	public City(String city, Country country) {
		this.city = city;
		this.country = country;
	}

	public City(Location location) {
		this.city = location.getCity();
		this.country = new Country(location.getCountry());
	}

	public Country getCountry() {
		return country;
	}

	public String getCity() {
		return this.city;
	}
	
	public boolean isEmtpy() {
		return city.isEmpty();
	}

	@Override
	public String toString() {
		return city.isEmpty() ? "-" : city;
	}
}
