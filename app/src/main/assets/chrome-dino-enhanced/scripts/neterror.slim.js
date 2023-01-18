// Copyright 2013 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

const HIDDEN_CLASS = 'hidden';

// Subframes use a different layout but the same html file.  This is to make it
// easier to support platforms that load the error page via different
// mechanisms (Currently just iOS). We also use the subframe style for portals
// as they are embedded like subframes and can't be interacted with by the user.
let isSubFrame = false;
if (window.top.location !== window.location || window.portalHost) {
    document.documentElement.setAttribute('subframe', '');
    isSubFrame = true;
}

// Adds an icon class to the list and removes classes previously set.
function updateIconClass(newClass) {
    const frameSelector = isSubFrame ? '#sub-frame-error' : '#main-frame-error';
    const iconEl = document.querySelector(frameSelector + ' .icon');

    if (iconEl.classList.contains(newClass)) {
        return;
    }

    iconEl.className = 'icon ' + newClass;
}


function onDocumentLoad() {
    const iconClass = loadTimeData.valueExists('iconClass') &&
        loadTimeData.getValue('iconClass');
    updateIconClass(iconClass);
    if (!isSubFrame && iconClass === 'icon-offline') {
        document.documentElement.classList.add('offline');
        // Mounted to window and called by android webview
        window.runner = new Runner('.interstitial-wrapper');
    }
}
document.addEventListener('DOMContentLoaded', onDocumentLoad);