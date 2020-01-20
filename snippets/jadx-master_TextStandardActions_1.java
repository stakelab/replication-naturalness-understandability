private void initActions() {
		undoAction = new AbstractAction(NLS.str("popup.undo")) {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (undoManager.canUndo()) {
					undoManager.undo();
				}
			}
		};
		redoAction = new AbstractAction(NLS.str("popup.redo")) {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (undoManager.canRedo()) {
					undoManager.redo();
				}
			}
		};
		cutAction = new AbstractAction(NLS.str("popup.cut")) {
			@Override
			public void actionPerformed(ActionEvent ae) {
				textComponent.cut();
			}
		};
		copyAction = new AbstractAction(NLS.str("popup.copy")) {
			@Override
			public void actionPerformed(ActionEvent ae) {
				textComponent.copy();
			}
		};
		pasteAction = new AbstractAction(NLS.str("popup.paste")) {
			@Override
			public void actionPerformed(ActionEvent ae) {
				textComponent.paste();
			}
		};
		deleteAction = new AbstractAction(NLS.str("popup.delete")) {
			@Override
			public void actionPerformed(ActionEvent ae) {
				textComponent.replaceSelection("");
			}
		};
		selectAllAction = new AbstractAction(NLS.str("popup.select_all")) {
			@Override
			public void actionPerformed(ActionEvent ae) {
				textComponent.selectAll();
			}
		};
	}