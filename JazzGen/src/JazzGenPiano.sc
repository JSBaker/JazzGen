/*	
====================================================================================
2011 | Jonathan Baker  | JazzGen  |  http://www.jonny-baker.com | http://github.com/JSBaker

All source code licenced under The MIT Licence
====================================================================================  
*/

+ JazzGen {

jazzPiano {

	{
	(8*tempMult).do{arg pc; //piano counter
//					"piano: ".post;
//					probs[pc][1].postln;
	
	if(solo<0,{
	
	// ======================= Chords =======================
	
		if(((probs[pc][1].squared.coin)||(pc==0)), {
			
			if(((playChord)&&(probs[pc][1].cubed.coin)),{
				// free piano synths if chord has been played
				if(freeFlag, {piano.do{arg item; item.free}});
				piano = Array.fill(4, {arg n; 
					var note;
					note = scale[root+chord7th[n]]+12;
					if((n==1)&&chordVar,{note=note+1});
					//t.postln;
					Synth(\piano, [\note, note ])
				});

				freeFlag=true;
				pianoTimes[pc].wait;
				
			},{
				
				
				
				if((playChord)&&((pc==0&&barCount==0)||(probs[pc][1].coin)||tempoEnd),{

					// free piano synths if chord has been played
					if(freeFlag, {piano.do{arg item; item.free}});
					//piano = Array.fill(4, {arg n; Synth(\piano, [\note, scale[root+chord7th[n]]])});
					piano = Array.fill(4, {arg n; 
						var note;
						note = scale[root+chord7th[n]];
						if((n==1)&&chordVar,{note=note+1});
						//t.postln;
						Synth(\piano, [\note, note ])
					});
						
					playChord = false;
					freeFlag = true;
					pianoTimes[pc].wait;
					
					
					},{
					
					// ======================= Separate Keys =======================
					
					if(probs[pc][1].coin,{

						if(0.8.coin,{

							chordInd = chord7th.choose;
							
							keyNote = scale[root+chordInd]+12;
																			if((chordInd==2)&&chordVar,{keyNote=keyNote+1});

							pianoKey = Synth(\piano,[\note, keyNote, \release, pKeyRelease]);

							(pianoTimes[pc]*0.667).wait;

							// more chance of playing second note if first has played
							if(0.9.coin,{

								chordInd = chord7th.choose;
								
								keyNote = scale[root+chordInd]+12;
								if((chordInd==2)&&chordVar,{keyNote=keyNote+1});
								pianoKey = Synth(\piano,[\note, keyNote, \release, pKeyRelease]);
								(pianoTimes[pc]*0.333).wait;
								},{
								(pianoTimes[pc]*0.333).wait;})	
						},{
							(pianoTimes[pc]*0.667).wait;
							if(0.4.coin,{
								chordInd = chord7th.choose;
								
								keyNote = scale[root+chordInd]+12;
								if((chordInd==2)&&chordVar,{keyNote=keyNote+1});
								pianoKey = Synth(\piano,[\note, keyNote, \release, pKeyRelease]);

								(pianoTimes[pc]*0.333).wait;
							},{
								(pianoTimes[pc]*0.333).wait;
							})
						})
					},{

						// release piano synths if chord has been played
						if((freeFlag==true), {piano.do{arg item; item.release}});
						freeFlag=false;
						pianoTimes[pc].wait;
					
					})
				})	
			})
		},{
			pianoTimes[pc].wait;			
		})
			
		},{
		
		if(solo==1,{
//var ps = (1.0-(scores[1]/0.025));
		
		if((pc==4)&&(ps.coin),{playChord=true});
		
		
		// ============ chords =============
		
		//calculate individual score for solo
			ps = (1.0-(scores[1]/gaussianThresh));
			
			if(pc==0,{	
				
				if(ps.sqrt.coin,{

					if(freeFlag, {piano.do{arg item; item.free}});
					piano = Array.fill(4, {arg n; Synth(\piano, [\note, scale[root+chord7th[n]], \ampMult,soloistAmp ])});
					playChord = false;
					freeFlag = true;
					pianoTimes[pc].wait;
					},{

					if(freeFlag, {piano.do{arg item; item.free}});

					piano = Array.fill(4, {arg n; Synth(\piano, [\note, scale[root+chord7th[n]]+12, \ampMult,soloistAmp ])});
					freeFlag = true;
					pianoTimes[pc].wait;
					})
				},{	
					if((playChord)&&((ps.squared).squared.coin),{
						
						if(ps.coin,{
							if(freeFlag, {piano.do{arg item; item.free}});

							piano = Array.fill(4, {arg n; Synth(\piano, [\note, scale[root+chord7th[n]]+12, \ampMult,soloistAmp ])});
							freeFlag = true;
							pianoTimes[pc].wait;
						},{
							if(freeFlag, {piano.do{arg item; item.free}});

							piano = Array.fill(4, {arg n; Synth(\piano, [\note, scale[root+chord7th[n]], \ampMult,soloistAmp ])});
							freeFlag = true;
							playChord = false;
							pianoTimes[pc].wait;
						});

					},{

						pianoTimes[pc].wait;

					})
						
				})
				
			},{
			
			//============= Accompaniment ========================
		
			if(pc==0,{
					if(freeFlag, {piano.do{arg item; item.free}});
					piano = Array.fill(4, {arg n; Synth(\piano, [\note, scale[root+chord7th[n]],\ampMult,soloAcAmp])});
					freeFlag=true;
					pianoTimes[pc].wait;
				},{	
					if(probs[pc][1].coin,{
						if((pc>5)&&(probs[pc][1].squared.coin),{
						
							pianoKey = Synth(\piano, [\note, scale[root+soloNote[pc]]+12, \release, pKeyRelease, \ampMult,soloAcAmp]);
							pianoTimes[pc].wait;
						
						},{
							
							pianoTimes[pc].wait;
							
						})
					},{
						if(freeFlag, {piano.do{arg item; item.release}});
						freeFlag=false;
						pianoTimes[pc].wait;
					})
				
						
				})
			})
		
		})
	
	}
	
}.fork(tempo);

//loop for piano key solo
if(solo==1,{	
	ps = (1.0-(scores[1]/gaussianThresh));
	
	{
		(8*tempMult).do{arg pk; // piano key counter
		// ============ separate keys =============
			

			if(soloPause==false,{

				psNote = psoloNote[pk];
				if(ps.coin, {
					
					if(ps.coin,{
					

						//probability for triplets increases on slower tempos
						trip = ((1.0-((min(bps,0.95)-0.4)/0.551)).squared);

						if((trip.coin)&&(ps.squared.coin),{
						
							pianoKey = Synth(\piano, [\note, scale[root+psNote]+pkeyOct, \release, pKeyRelease, \ampMult,soloistAmp]);
							
							(pianoTimes[pk]*0.333).wait;
							
							psVar=[0,1,2].wchoose(noteVarWeights[1]);
							pianoKey = Synth(\piano, [\note, scale[root+psNote+(noteVar[psVar]*2)]+pkeyOct, \release, pKeyRelease, \ampMult,soloistAmp]);
							
							(pianoTimes[pk]*0.333).wait;
							
							psVar=[0,1,2].wchoose(noteVarWeights[psVar]);

							pianoKey = Synth(\piano, [\note, scale[root+psNote+(noteVar[psVar]*2)]+pkeyOct, \release, pKeyRelease, \ampMult,soloistAmp]);
							
							(pianoTimes[pk]*0.334).wait;
							},{
						
							(pianoTimes[pk]*0.667).wait;

							pianoKey = Synth(\piano, [\note, scale[root+(psNote)]+pkeyOct, \release, pKeyRelease, \ampMult,soloistAmp]);
							
							(pianoTimes[pk]*0.333).wait;
							})
						},{
						pianoKey = Synth(\piano, [\note, scale[root+psNote]+pkeyOct, \release, pKeyRelease, \ampMult,soloistAmp]);
						
						(pianoTimes[pk]*0.667).wait;
						
						psVar=noteVar.wchoose(noteVarWeights[1]);
						pianoKey = Synth(\piano, [\note, scale[root+(psNote+(psVar*2))]+pkeyOct, \release, pKeyRelease, \ampMult,soloistAmp]);
						
						(pianoTimes[pk]*0.333).wait;
						})
					},{
					if(sPause.coin, 
					{
						soloPause = true;sPause = 0.01;"solo pause".postln
					},{
						sPause = sPause*1.05
					});

					pianoKey = Synth(\piano, [\note, scale[root+psNote]+pkeyOct, \release, pKeyRelease, \ampMult,soloistAmp]);
					
					pianoTimes[pk].wait;
					}
				)
			},{
				pianoTimes[pk].wait;
			})
			
		}
	}.fork(tempo);
});
	


}

}