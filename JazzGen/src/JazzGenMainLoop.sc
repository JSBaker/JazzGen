/*	
====================================================================================
2011 | Jonathan Baker  | JazzGen  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/

+ JazzGen {

setMainLoop {

mainLoop = Task.new({
	
		
		inf.do({arg j;
		
		tot = 0;	
		playChord = true;
		
		if(j==viChord,{
				chordVar = if(((solo<0)&&(0.25.coin)),{"vi chord change".postln;true},{false});
				viChord = viChord+4;
			},{
				chordVar=false;
		});
		
		
		
		//change key at some point ?
//		if(j&16==0,{ 	//bps = rrand(0.4,0.95);
//					keySigNote = rrand(48,59);
//					//tempo = TempoClock(bps);
//					"bps: ".post; bps.postln;
//					"key: ".post; keySigNote.postln;
//					p = ((bps-0.38)/0.53);
//					p.postln;
//					intervals = if(p.coin,{"Major".postln; [2,2,1,2,2,2,1]},{"Minor".postln; [2,1,2,2,1,2,2]});
//					if(0.5.coin,{intervals = [2,1,2,2,2,2,1];"jazz takeover!".postln;});
//				});
				
		
		if(j%sectionLength==0,{
				currentSolo=0;if(lastSolo!=solo,{barCount=0;tempoBoost=0});
			},{
				barCount=barCount+1;
		});
		
		
		"barCount: ".post; barCount.postln;
		
		if(barCount%4==0,{
			chordPat = if(0.6.coin, {[7,12,8,11]},{[7,5,8,4]});
			chordPat.postln;
		});
		
		// first chord switch to chord 3 in scale?
		root = if((((j%sectionLength)!=0)&&(j%4==0)&&(0.35.coin)),{"chordchange".postln;9},{chordPat.wrapAt(j)});
		
		if((barCount==0)&&(lastSolo>(-1))&&(lastSolo!=solo),{Synth(\buff,[\bufnum, buffers[5],\amp, 0.2]); sectionScores=Array.fill(3,{0})});
		
		// increase tempo?
		if((solo<0)&&(barCount>3)&&(((1-(bps.squared)).coin)||tempoChange)&&(min(tempoBoost*0.05,0.85).coin),{
		
				tempo = TempoClock(bps*2); tempMult = 2; "tempo change...!".postln;tempoChange=true
			},{
				tempo=origTempo; tempMult = 1; if(tempoChange,{tempoBoost=0;tempoChange=false; tempoEnd = true;})
			}
		);
		
		scores = Array.newClear((8*tempMult)*3);
		probs = Array.fill((8*tempMult),{Array.newClear(3)});
		
		bassTimes = Array.newClear(8*tempMult);
		pianoTimes = Array.newClear(8*tempMult);
		drumTimes = Array.newClear(8*tempMult);
		
		bassTot = pianoTot = drumTot = 0.0;
			
		(8*tempMult).do{arg a;
			
			if((a+1)%4!=0,{
				3.do{arg b;
					scores[(a*3)+b] = gaussianThresh.sum3rand;
					probs[a][b] = scores[(a*3)+b];
				};
				
				bassTimes[a] = scores[(a*3)+0]+notelength;
				pianoTimes[a] = scores[(a*3)+1]+notelength;
				drumTimes[a] = scores[(a*3)+2]+notelength;
				
				bassTot = bassTot + bassTimes[a];
				pianoTot = pianoTot + pianoTimes[a];
				drumTot = drumTot + drumTimes[a];
				
			},{
				bassTimes[a] =  (1.0-bassTot);//.postln;
				pianoTimes[a] = (1.0-pianoTot);//.postln;
				drumTimes[a] = (1.0-drumTot);//.postln;

				scores[(a*3)+0] = bassTimes[a]-notelength;
				scores[(a*3)+1] = pianoTimes[a]-notelength;
				scores[(a*3)+2] = drumTimes[a]-notelength;
				
				3.do{arg b;
					probs[a][b] = scores[(a*3)+b];
				};
				
				bassTot = pianoTot = drumTot = 0.0;
			});
			
			probs[a]= 1-(probs[a].abs.normalizeSum);
		};
	 	
	 	if(solo!=(-1),{
	 		4.do{arg a;
	 			drumSoloTimes[a] = drumTimes[(a*2)] + drumTimes[(a*2)+1];
	 		};
	 		
	 		currentNote = ([0,1,2,3,4].wchoose(soloWeights[currentNote]));
			soloNote = soloNotes[currentNote];
			soloPause = false;
			
			if(solo==1,{
				pcurrentNote = ([0,1,2,3,4].wchoose(soloWeights[pcurrentNote]));
				psoloNote = psoloNotes[pcurrentNote];
			});
	 	});
	 		
	 	scores=scores.abs;
		 
		3.do{arg a;
			var score=0;
			(8*tempMult).do{arg b;
				score = score + probs[b][a];
			};
			//score.postln;
			sectionScores[a] = sectionScores[a] + score;
			
		};
		
		////////////
	
	//call the instrument loop methods
	this.jazzBass;
	this.jazzPiano;
	this.jazzDrums;
	
	
	
	/////////////////	
	
		2.wait;	
	diff = prev = 0.0;

	// if standard playing and scores are relatively even, increase probability of tempo being doubled
	if(solo<0,{
		scores.do{arg item, g; if(g>0, {
		
									diff = diff + ((item - prev).abs);prev=item;//diff.post;//" : ".post;
								},{
									prev=item;
								})
						};
		if(diff<tempoThresh,{tempoBoost = tempoBoost+1.2;"tempoBoost++".postln;});
	});
	
	// determine if any instrument has scored higher than threshold against closest score and set them as soloist
	if(((j+1)%sectionLength==0),{


			if(soloForce,{
				lastSolo = solo;
				solo = forcedSolo;
				soloForce=false;
			},{
			lastSolo = solo;
			solo = -1;
			best = 0;
			
			sectionScores.do{arg item, n;
				var hiCount = 0;
				if(item>best,{
					best=item;
					
					sectionScores.do{arg item2;
						if((item-item2)>soloThresh,{hiCount=hiCount+1})
						};
					if(hiCount>1,{solo=n});
				});
			};
			
			if(soloRepeat||((solo==lastSolo)&&((bps.squared.coin)==false)),{solo = -1;soloRepeat=false},{soloRepeat=true});
			
			
					
			});	
			
			if(solo<0,{
				"Standard Play".postln;
				},{
				soloRepeat.postln;
				if(solo==0,{
					"Bass Solo!".postln;
					},{
					if(solo==1,{
						"Piano Solo!".postln;
						},{
						"Drum Solo!".postln;
					})
				})
			});

	});
	
});

	
});

}

}