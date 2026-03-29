package com.credit.bridge.remote.bean

class StorageInfo {
    var aSpace: Int = 0
    var rSpace: Int = 0

    var memTotal: Long = 0
    var memFree: Long= 0
    var buffers: Long= 0
    var cached: Long= 0
    var wwapCached: Long= 0   //
    var active: Long= 0        // /
    var inactive: Long= 0      // /
    var activeAnon: Long= 0    //
    var inactiveAnon: Long= 0  //
    var activeFile: Long= 0   //
    var inactiveFile: Long= 0 //
    var unevictable: Long= 0   //
    var mlocked: Long= 0
    var highTotal: Long= 0
    var highFree: Long= 0
    var lowTotal: Long= 0
    var lowFree: Long= 0
    var swapTotal: Long= 0
    var swapFree: Long= 0
    var dirty: Long= 0
    var writeback: Long= 0
    var anonPages: Long= 0
    var mapped: Long= 0
    var shmem: Long= 0
    var slab: Long= 0
    var sreclaimable: Long= 0
    var sunreclaim: Long= 0
    var kernelStack: Long= 0
    var pageTables: Long= 0
    var nfsUnstable: Long= 0
    var bounce: Long= 0
    var writebackTmp: Long= 0
    var commitLimit: Long= 0
    var committedAs: Long= 0
    var vmallocTotal: Long= 0
    var vmallocUsed: Long= 0
    var vmallocChunk: Long= 0
    var freeCma: Long= 0
    var cmaTotal: Long = 0
}