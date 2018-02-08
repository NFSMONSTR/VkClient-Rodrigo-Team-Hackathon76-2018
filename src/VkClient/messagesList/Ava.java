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

package VkClient.messagesList;

import javafx.scene.image.Image;

public class Ava {
    int id;
    Image img;
    String name;
    public Ava(int id, String name, String image) {
        this.id = id;
        this.name = name;
        img = new Image(image,true);
    }

    public Integer getId() {
        return id;
    }

    public Image getImg() {
        return img;
    }

    public String getName() {
        return name;
    }
}
