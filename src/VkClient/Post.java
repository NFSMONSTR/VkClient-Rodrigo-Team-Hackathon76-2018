/*
    VkClient - client for vk.com
    Copyright (C) 2018  Belyaev Maxim(NFS_MONSTR), Aleksandr Novozhilov(MrLolthe1st), Grigoriy Zhukov

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

package VkClient;

public class Post {
    private String name;
    private String text;
    private String image;
    private String ava;
    private String date;

    public Post(String name, String text, String image, String ava, String date) {
        this.ava = ava;
        this.image = image;
        this.name = name;
        this.text = text;
        this.date = date;
    }

    public String getAva() {
        return ava;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getDate() {
        return date;
    }
}
