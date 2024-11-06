/*
* Copyright (c) 2024, Patrick Wilmes <p.wilmes89@gmail.com>
* All rights reserved.
*
* SPDX-License-Identifier: BSD-2-Clause
*/
package com.bitlake.repository

import java.nio.file.Paths

object Context {
    private val wd = System.getProperty("user.dir")
    val repository = Repository.getInstance(Paths.get(wd).toAbsolutePath())
}